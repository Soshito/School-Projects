from __future__ import annotations

import hashlib
import json
import logging
from logging.handlers import RotatingFileHandler
from pathlib import Path
from datetime import datetime
from typing import Any, Dict

try:
    from loginPage import DatabaseManager
except ImportError:
    DatabaseManager = Any


TARGET_EXTS = {
    ".txt", ".docx", ".pdf", ".jpg", ".jpeg", ".png",
    ".mp4", ".mp3", ".zip", ".db", ".csv", ".xlsx", ".pptx"
}

SKIP_DIRS = {"simulation_output"}
SENTINEL_FILENAME = ".SIMULATION_ROOT"

RANSOM_NOTE_TEXT = """\
*** YOUR FILES HAVE BEEN LOCKED (SIMULATION) ***

This is a SAFE classroom simulation. No real encryption was performed.
Your original files are still intact.

Artifacts created:
- simulation_output/LOCKED_VIEW (parallel "locked" view)
- simulation_output/manifest.json (scope + hashes)
- simulation_output/sim.log + events.jsonl (detailed logging)
"""


def setup_logger(out_root: Path) -> logging.Logger:
    out_root.mkdir(parents=True, exist_ok=True)

    logger = logging.getLogger("ransom_sim")
    logger.setLevel(logging.INFO)
    logger.handlers.clear()
    logger.propagate = False

    ch = logging.StreamHandler()
    ch.setLevel(logging.INFO)
    ch.setFormatter(logging.Formatter("[%(asctime)s] %(levelname)s: %(message)s"))
    logger.addHandler(ch)

    fh = RotatingFileHandler(
        out_root / "sim.log",
        maxBytes=500_000,
        backupCount=3,
        encoding="utf-8"
    )
    fh.setLevel(logging.INFO)
    fh.setFormatter(logging.Formatter("%(asctime)s %(levelname)s %(message)s"))
    logger.addHandler(fh)

    return logger


class EventWriter:
    def __init__(
        self,
        path: Path,
        db_manager: DatabaseManager = None,
        user_id: int = None,
        run_id: str = None
    ):
        self.path = path
        self.path.parent.mkdir(parents=True, exist_ok=True)
        self.db = db_manager
        self.user_id = user_id
        self.run_id = run_id

    def emit(
        self,
        event_type: str,
        severity: str = "INFO",
        phase: str = "GENERAL",
        message: str = "",
        **data: Any
    ) -> None:
        payload = {
            "ts": datetime.now().isoformat(),
            "type": event_type,
            "run_id": self.run_id,
            "severity": severity,
            "phase": phase,
            "message": message,
            **data
        }

        with self.path.open("a", encoding="utf-8") as f:
            f.write(json.dumps(payload) + "\n")

        if self.db and getattr(self.db, "connection", None):
            try:
                cursor = self.db.connection.cursor()
                query = """
                    INSERT INTO simulation_logs
                    (timestamp, event_type, file_path, details, simulation_run_id, user_id)
                    VALUES (%s, %s, %s, %s, %s, %s)
                """

                file_path = (
                    data.get("relative_path")
                    or data.get("locked_relative_path")
                    or data.get("note_relative_path")
                    or ""
                )

                cursor.execute(
                    query,
                    (
                        datetime.now(),
                        event_type,
                        file_path,
                        json.dumps(payload),
                        self.run_id,
                        self.user_id
                    )
                )
                self.db.connection.commit()
                cursor.close()
            except Exception as e:
                print(f"[DB Logging Error] {e}")


def sha256_file(path: Path) -> str:
    h = hashlib.sha256()
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def ensure_sentinel(root: Path) -> None:
    sentinel = root / SENTINEL_FILENAME
    if not sentinel.exists():
        raise RuntimeError(
            f"Refusing to run: sentinel file missing at {sentinel}\n"
            f"Create it (empty file) to confirm you're in the lab directory."
        )


def should_skip(path: Path) -> bool:
    return any(part in SKIP_DIRS for part in path.parts)


def is_target_file(path: Path) -> bool:
    return path.is_file() and path.suffix.lower() in TARGET_EXTS


def write_locked_copy(dst: Path, meta: Dict[str, Any]) -> None:
    dst.parent.mkdir(parents=True, exist_ok=True)
    marker = (
        "=== LOCKED FILE (SIMULATION) ===\n"
        "No encryption performed. This is a harmless placeholder.\n\n"
        f"Original path: {meta['relative_path']}\n"
        f"Original size: {meta['size_bytes']} bytes\n"
        f"SHA-256: {meta['sha256']}\n"
        f"Locked at: {meta['timestamp']}\n"
        f"Run ID: {meta['run_id']}\n"
    )
    dst.write_text(marker, encoding="utf-8")


def run_simulation(
    project_files_root: Path,
    db_manager: DatabaseManager = None,
    user_id: int = None
) -> Path:
    root = project_files_root.resolve()
    ensure_sentinel(root)

    out_root = root / "simulation_output"
    locked_view = out_root / "LOCKED_VIEW"
    run_id = datetime.now().strftime("%Y%m%d_%H%M%S")

    logger = setup_logger(out_root)
    events = EventWriter(
        out_root / "events.jsonl",
        db_manager=db_manager,
        user_id=user_id,
        run_id=run_id
    )

    stats: Dict[str, int] = {
        "paths_discovered": 0,
        "files_scanned": 0,
        "files_skipped": 0,
        "files_targeted": 0,
        "files_locked": 0,
        "notes_written": 0,
        "errors": 0
    }

    events.emit(
        "run_start",
        severity="INFO",
        phase="INITIALIZATION",
        message="Safe ransomware simulation started",
        root=str(root),
        safe_mode=True,
        target_extensions=sorted(TARGET_EXTS),
        skip_directories=sorted(SKIP_DIRS)
    )
    logger.info("Simulation starting (SAFE mode). Root=%s", root)

    manifest: Dict[str, Any] = {
        "simulation": True,
        "safe_mode": True,
        "run_id": run_id,
        "root": str(root),
        "generated_at": datetime.now().isoformat(),
        "targets": [],
        "config": {
            "target_exts": sorted(TARGET_EXTS),
            "skip_dirs": sorted(SKIP_DIRS),
            "original_files_untouched": True,
        },
        "stats": stats
    }

    all_paths = list(root.rglob("*"))
    stats["paths_discovered"] = len(all_paths)

    events.emit(
        "scan_start",
        severity="INFO",
        phase="DISCOVERY",
        message="Recursive scan started",
        total_paths=len(all_paths)
    )

    logger.info("Discovery started. Total paths discovered=%d", len(all_paths))

    notes_written_dirs = set()

    for path in all_paths:
        rel = path.relative_to(root)

        if path.is_dir():
            events.emit(
                "directory_discovered",
                severity="INFO",
                phase="DISCOVERY",
                message="Directory observed during scan",
                relative_path=str(rel)
            )
            continue

        stats["files_scanned"] += 1

        events.emit(
            "file_discovered",
            severity="INFO",
            phase="DISCOVERY",
            message="File observed during scan",
            relative_path=str(rel),
            extension=path.suffix.lower(),
            is_target_candidate=path.suffix.lower() in TARGET_EXTS
        )

        if should_skip(path):
            stats["files_skipped"] += 1
            events.emit(
                "file_skipped",
                severity="INFO",
                phase="DISCOVERY",
                message="Skipped because path is in excluded directory",
                relative_path=str(rel),
                reason="excluded_directory"
            )
            logger.info("Skipped excluded path: %s", rel)
            continue

        if not is_target_file(path):
            stats["files_skipped"] += 1
            events.emit(
                "file_skipped",
                severity="INFO",
                phase="TARGETING",
                message="Skipped because extension is not targeted",
                relative_path=str(rel),
                extension=path.suffix.lower(),
                reason="non_target_extension"
            )
            logger.info("Skipped non-target file: %s", rel)
            continue

        stats["files_targeted"] += 1
        events.emit(
            "target_selected",
            severity="INFO",
            phase="TARGETING",
            message="Target file selected for simulated locking",
            relative_path=str(rel),
            extension=path.suffix.lower()
        )
        logger.info("Target selected: %s", rel)

        try:
            events.emit(
                "metadata_collection_start",
                severity="INFO",
                phase="COLLECTION",
                message="Collecting file metadata before locking",
                relative_path=str(rel)
            )

            meta = {
                "relative_path": str(rel),
                "size_bytes": path.stat().st_size,
                "sha256": sha256_file(path),
                "timestamp": datetime.now().isoformat(),
                "run_id": run_id
            }

            events.emit(
                "metadata_collection_complete",
                severity="INFO",
                phase="COLLECTION",
                message="Metadata collected for target file",
                relative_path=str(rel),
                size_bytes=meta["size_bytes"],
                sha256=meta["sha256"]
            )

            dst = locked_view / rel
            dst = dst.with_name(dst.name + ".locked")

            events.emit(
                "lock_start",
                severity="WARNING",
                phase="LOCKING",
                message="Beginning simulated file lock operation",
                relative_path=str(rel),
                locked_relative_path=str(dst.relative_to(root))
            )

            write_locked_copy(dst, meta)

            manifest["targets"].append(meta)
            stats["files_locked"] += 1

            events.emit(
                "locked_written",
                severity="WARNING",
                phase="LOCKING",
                message="Locked placeholder created to simulate denied file access",
                relative_path=str(rel),
                locked_relative_path=str(dst.relative_to(root))
            )
            logger.warning("Locked placeholder created: %s", dst.relative_to(root))

            note_dir = dst.parent
            if note_dir not in notes_written_dirs:
                note_path = note_dir / "RANSOM_NOTE.txt"
                note_path.write_text(RANSOM_NOTE_TEXT, encoding="utf-8")
                notes_written_dirs.add(note_dir)
                stats["notes_written"] += 1

                events.emit(
                    "ransom_note_written",
                    severity="WARNING",
                    phase="IMPACT",
                    message="Ransom note written to increase visible impact",
                    note_relative_path=str(note_path.relative_to(root))
                )
                logger.warning("Ransom note written: %s", note_path.relative_to(root))

        except Exception as e:
            stats["errors"] += 1
            events.emit(
                "file_access_error",
                severity="ERROR",
                phase="LOCKING",
                message=str(e),
                relative_path=str(rel)
            )
            logger.error("Failed processing %s: %s", rel, e)

    manifest_path = out_root / "manifest.json"
    manifest_path.write_text(json.dumps(manifest, indent=2), encoding="utf-8")

    events.emit(
        "manifest_written",
        severity="INFO",
        phase="FINALIZATION",
        message="Manifest written",
        relative_path=str(manifest_path.relative_to(root))
    )

    events.emit(
        "summary",
        severity="INFO",
        phase="FINALIZATION",
        message="Simulation summary generated",
        stats=stats
    )

    events.emit(
        "run_complete",
        severity="INFO",
        phase="FINALIZATION",
        message="Simulation complete",
        stats=stats
    )

    logger.info(
        "Simulation complete. scanned=%d skipped=%d targeted=%d locked=%d notes=%d errors=%d",
        stats["files_scanned"],
        stats["files_skipped"],
        stats["files_targeted"],
        stats["files_locked"],
        stats["notes_written"],
        stats["errors"]
    )

    return out_root


if __name__ == "__main__":
    repo_root = Path(__file__).resolve().parent
    project_files_root = repo_root / "project_files"
    run_simulation(project_files_root)