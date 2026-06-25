import tkinter as tk
from tkinter import ttk, messagebox, filedialog
import csv
import json
from pathlib import Path
from datetime import datetime


class LogsPage:
    """
    Displays simulation logs from database with JSON file fallback.
    Color-codes log rows by ransomware phase and adds a detailed description field.
    Includes a lower expandable details panel for fully reading long log content.
    """

    def __init__(self, parent, on_back=None, db_manager=None):
        self.parent = parent
        self.on_back = on_back or (lambda: None)
        self.db = db_manager

        self.tree = None
        self.search_entry = None
        self.filter_combo = None
        self.filter_var = None
        self.summary_label = None
        self.details_text = None

        self.colors = {
            "bg_dark": "#1e1e1e",
            "bg_medium": "#273238",
            "bg_light": "#34434a",
            "header_teal": "#4fbdba",
            "text_light": "#ffffff",
            "text_dark": "#1e1e1e",
            "accent": "#5f6f6f",

            # phase colors
            "phase_initialization": "#355c7d",   # cool blue
            "phase_discovery": "#2a6f97",        # cool blue
            "phase_recon": "#468faf",            # lighter cool blue
            "phase_collection": "#4d908e",       # teal
            "phase_targeting": "#577590",        # steel blue
            "phase_locking": "#8d6e00",          # hot yellow-brown
            "phase_impact": "#a44a3f",           # red-orange
            "phase_persistence": "#7b5ea7",      # purple
            "phase_finalization": "#3d5a80",     # cool navy
            "phase_general": "#4a5568",          # neutral
            "phase_error": "#7f1d1d",            # dark red
        }

        self.demo_logs = [
            {
                "timestamp": "2026-03-27 13:00:00",
                "severity": "INFO",
                "phase": "INITIALIZATION",
                "event_type": "run_start",
                "details": "simulation started (demo)",
                "description": "The simulation has begun and is preparing its internal state, log pipeline, and target environment before any file activity occurs.",
                "stats": None,
            },
            {
                "timestamp": "2026-03-27 13:00:03",
                "severity": "INFO",
                "phase": "DISCOVERY",
                "event_type": "scan_start",
                "details": "Beginning recursive scan of project files (demo)",
                "description": "The ransomware is enumerating folders and files to understand what exists in the environment and decide what can be targeted later.",
                "stats": None,
            },
            {
                "timestamp": "2026-03-27 13:00:05",
                "severity": "WARNING",
                "phase": "LOCKING",
                "event_type": "locked_written",
                "details": "Documents/Resume.docx.locked (demo)",
                "description": "A placeholder locked file has been written to simulate encryption or file denial. This represents the attacker removing user access to important data.",
                "stats": None,
            },
            {
                "timestamp": "2026-03-27 13:00:15",
                "severity": "ERROR",
                "phase": "LOCKING",
                "event_type": "file_access_error",
                "details": "Pictures/photo1.jpg could not be locked (demo)",
                "description": "The ransomware attempted to lock a file but failed. In real attacks, this can happen because of permissions, file locks, corruption, or competing processes.",
                "stats": None,
            },
            {
                "timestamp": "2026-03-27 13:00:20",
                "severity": "WARNING",
                "phase": "IMPACT",
                "event_type": "ransom_note_written",
                "details": "Desktop/RANSOM_NOTE.txt (demo)",
                "description": "A ransom note was dropped to notify the victim that files are inaccessible and to pressure them into following attacker instructions.",
                "stats": None,
            },
            {
                "timestamp": "2026-03-27 13:00:32",
                "severity": "INFO",
                "phase": "FINALIZATION",
                "event_type": "summary",
                "details": "scanned=12 skipped=4 targeted=8 locked=8 notes=3 errors=0",
                "description": "The simulation is wrapping up and recording its final activity counts so the user can review what was discovered, targeted, and successfully impacted.",
                "stats": {
                    "files_scanned": 12,
                    "files_skipped": 4,
                    "files_targeted": 8,
                    "files_locked": 8,
                    "notes_written": 3,
                    "errors": 0,
                },
            },
        ]

        self.all_logs = []

        self.setup_styles()
        self.create_widgets()
        self.load_logs()

    def setup_styles(self):
        style = ttk.Style()
        style.theme_use("default")

        style.configure(
            "Custom.Treeview",
            background=self.colors["bg_medium"],
            foreground=self.colors["text_light"],
            fieldbackground=self.colors["bg_medium"],
            rowheight=28,
            borderwidth=0,
            relief="flat",
            font=("Arial", 10),
        )

        style.configure(
            "Custom.Treeview.Heading",
            background=self.colors["bg_light"],
            foreground=self.colors["text_light"],
            font=("Arial", 10, "bold"),
            relief="flat",
        )

        style.map(
            "Custom.Treeview",
            background=[("selected", self.colors["header_teal"])],
            foreground=[("selected", self.colors["text_dark"])],
        )

        style.configure(
            "Custom.TCombobox",
            fieldbackground=self.colors["bg_medium"],
            background=self.colors["bg_light"],
            foreground=self.colors["text_light"],
        )

    def create_widgets(self):
        main_frame = tk.Frame(self.parent, bg=self.colors["bg_dark"])
        main_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

        title_frame = tk.Frame(main_frame, bg=self.colors["header_teal"], height=40)
        title_frame.pack(fill=tk.X, pady=(0, 10))
        title_frame.pack_propagate(False)

        tk.Label(
            title_frame,
            text="Logs Page",
            bg=self.colors["header_teal"],
            fg=self.colors["text_dark"],
            font=("Arial", 16, "bold", "italic"),
        ).pack(expand=True)

        self.summary_label = tk.Label(
            main_frame,
            text="No summary loaded",
            bg=self.colors["bg_dark"],
            fg=self.colors["text_light"],
            anchor="w",
            font=("Arial", 10, "bold"),
        )
        self.summary_label.pack(fill=tk.X, pady=(0, 10))

        controls_frame = tk.Frame(main_frame, bg=self.colors["bg_dark"])
        controls_frame.pack(fill=tk.X, pady=(0, 10))

        tk.Label(
            controls_frame,
            text="Filter:",
            bg=self.colors["bg_dark"],
            fg=self.colors["text_light"],
            font=("Arial", 10, "bold"),
        ).pack(side=tk.LEFT, padx=(0, 5))

        self.filter_var = tk.StringVar(value="All Events")
        self.filter_combo = ttk.Combobox(
            controls_frame,
            textvariable=self.filter_var,
            values=["All Events"],
            state="readonly",
            width=18,
            style="Custom.TCombobox",
        )
        self.filter_combo.pack(side=tk.LEFT, padx=(0, 20))
        self.filter_combo.bind("<<ComboboxSelected>>", self.filter_logs)

        tk.Label(
            controls_frame,
            text="Search:",
            bg=self.colors["bg_dark"],
            fg=self.colors["text_light"],
            font=("Arial", 10, "bold"),
        ).pack(side=tk.LEFT, padx=(0, 5))

        self.search_entry = tk.Entry(
            controls_frame,
            bg=self.colors["bg_medium"],
            fg=self.colors["text_light"],
            insertbackground=self.colors["text_light"],
            relief=tk.SUNKEN,
            bd=2,
            font=("Arial", 10),
        )
        self.search_entry.pack(side=tk.LEFT, fill=tk.X, expand=True, padx=(0, 10))
        self.search_entry.bind("<KeyRelease>", self.filter_logs)

        tk.Button(
            controls_frame,
            text="🔄 Reload",
            bg=self.colors["bg_medium"],
            fg=self.colors["text_light"],
            relief=tk.RAISED,
            bd=2,
            command=self.load_logs,
        ).pack(side=tk.LEFT)

        tree_frame = tk.Frame(main_frame, bg=self.colors["bg_dark"])
        tree_frame.pack(fill=tk.BOTH, expand=True, pady=(0, 10))

        vsb = ttk.Scrollbar(tree_frame, orient="vertical")
        vsb.pack(side=tk.RIGHT, fill=tk.Y)

        hsb = ttk.Scrollbar(tree_frame, orient="horizontal")
        hsb.pack(side=tk.BOTTOM, fill=tk.X)

        columns = ("timestamp", "severity", "phase", "event_type", "details", "description")
        self.tree = ttk.Treeview(
            tree_frame,
            columns=columns,
            show="headings",
            style="Custom.Treeview",
            yscrollcommand=vsb.set,
            xscrollcommand=hsb.set,
        )
        self.tree.pack(fill=tk.BOTH, expand=True)
        vsb.config(command=self.tree.yview)
        hsb.config(command=self.tree.xview)

        self.tree.heading("timestamp", text="Timestamp")
        self.tree.heading("severity", text="Severity")
        self.tree.heading("phase", text="Phase")
        self.tree.heading("event_type", text="Event Type")
        self.tree.heading("details", text="Details")
        self.tree.heading("description", text="Description")

        self.tree.column("timestamp", width=165, anchor="w")
        self.tree.column("severity", width=90, anchor="w")
        self.tree.column("phase", width=130, anchor="w")
        self.tree.column("event_type", width=170, anchor="w")
        self.tree.column("details", width=360, anchor="w")
        self.tree.column("description", width=700, anchor="w")

        self.tree.tag_configure("INITIALIZATION", background=self.colors["phase_initialization"], foreground="#ffffff")
        self.tree.tag_configure("DISCOVERY", background=self.colors["phase_discovery"], foreground="#ffffff")
        self.tree.tag_configure("RECON", background=self.colors["phase_recon"], foreground="#ffffff")
        self.tree.tag_configure("COLLECTION", background=self.colors["phase_collection"], foreground="#ffffff")
        self.tree.tag_configure("TARGETING", background=self.colors["phase_targeting"], foreground="#ffffff")
        self.tree.tag_configure("LOCKING", background=self.colors["phase_locking"], foreground="#ffffff")
        self.tree.tag_configure("IMPACT", background=self.colors["phase_impact"], foreground="#ffffff")
        self.tree.tag_configure("PERSISTENCE", background=self.colors["phase_persistence"], foreground="#ffffff")
        self.tree.tag_configure("FINALIZATION", background=self.colors["phase_finalization"], foreground="#ffffff")
        self.tree.tag_configure("GENERAL", background=self.colors["phase_general"], foreground="#ffffff")
        self.tree.tag_configure("ERROR_PHASE", background=self.colors["phase_error"], foreground="#ffffff")
        self.tree.tag_configure("DEFAULT", background=self.colors["phase_general"], foreground="#ffffff")

        self.tree.bind("<<TreeviewSelect>>", self.on_row_select)

        details_frame = tk.Frame(main_frame, bg=self.colors["bg_dark"])
        details_frame.pack(fill=tk.BOTH, expand=False, pady=(0, 10))

        tk.Label(
            details_frame,
            text="Selected Log Details",
            bg=self.colors["bg_dark"],
            fg=self.colors["text_light"],
            font=("Arial", 10, "bold"),
            anchor="w",
        ).pack(fill=tk.X, pady=(0, 4))

        details_text_frame = tk.Frame(details_frame, bg=self.colors["bg_dark"])
        details_text_frame.pack(fill=tk.BOTH, expand=True)

        details_scroll = ttk.Scrollbar(details_text_frame, orient="vertical")
        details_scroll.pack(side=tk.RIGHT, fill=tk.Y)

        self.details_text = tk.Text(
            details_text_frame,
            height=8,
            bg=self.colors["bg_medium"],
            fg=self.colors["text_light"],
            insertbackground=self.colors["text_light"],
            wrap="word",
            relief=tk.SUNKEN,
            bd=2,
            font=("Arial", 10),
            yscrollcommand=details_scroll.set,
        )
        self.details_text.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        details_scroll.config(command=self.details_text.yview)

        self.details_text.insert(
            "1.0",
            "Select a log row above to view the full Details and Description text."
        )
        self.details_text.config(state=tk.DISABLED)

        bottom_frame = tk.Frame(main_frame, bg=self.colors["bg_dark"])
        bottom_frame.pack(fill=tk.X)

        tk.Button(
            bottom_frame,
            text="export logs",
            bg=self.colors["bg_medium"],
            fg=self.colors["text_light"],
            font=("Arial", 10, "bold"),
            relief=tk.RAISED,
            bd=3,
            padx=20,
            pady=5,
            command=self.export_logs,
        ).pack(side=tk.LEFT)

        tk.Button(
            bottom_frame,
            text="<Back",
            bg=self.colors["bg_medium"],
            fg=self.colors["text_light"],
            font=("Arial", 10, "bold"),
            relief=tk.RAISED,
            bd=3,
            padx=20,
            pady=5,
            command=self.go_back,
        ).pack(side=tk.RIGHT)

    def _events_path(self) -> Path:
        repo_root = Path(__file__).resolve().parent.parent
        return repo_root / "project_files" / "simulation_output" / "events.jsonl"

    def _format_ts(self, iso_ts: str) -> str:
        try:
            dt = datetime.fromisoformat(iso_ts)
            return dt.strftime("%Y-%m-%d %H:%M:%S")
        except Exception:
            return str(iso_ts)

    def _format_stats_details(self, stats: dict) -> str:
        return (
            f"scanned={stats.get('files_scanned', 0)} "
            f"skipped={stats.get('files_skipped', 0)} "
            f"targeted={stats.get('files_targeted', 0)} "
            f"locked={stats.get('files_locked', 0)} "
            f"notes={stats.get('notes_written', 0)} "
            f"errors={stats.get('errors', 0)}"
        )

    def _build_description(self, phase: str, event_type: str, details: str, event_data: dict = None) -> str:
        event_data = event_data or {}
        phase = str(phase or "GENERAL").upper()
        event_type = str(event_type or "event").lower()

        descriptions = {
            "run_start": (
                "The ransomware simulation is starting up. At this point it prepares internal state, "
                "initializes logging, and gets ready to inspect the controlled file environment."
            ),
            "scan_start": (
                "The malware is beginning file system discovery. This phase identifies directories and files "
                "that may be valuable targets for later locking or simulated encryption."
            ),
            "directory_discovered": (
                "A directory has been identified during discovery. Attackers map folders first so they can "
                "prioritize common user locations such as Documents, Desktop, Downloads, and Pictures."
            ),
            "file_discovered": (
                "A file has been found during the scan. This gives the ransomware visibility into what user "
                "content exists before deciding whether it should be targeted."
            ),
            "metadata_collection_complete": (
                "The ransomware collected metadata about a file, such as size or type. This helps it determine "
                "whether the file is worth targeting and how to process it."
            ),
            "target_selected": (
                "This file was selected as a target because it matches the ransomware's criteria, such as being "
                "a user file type or being located in a valuable directory."
            ),
            "target_skipped": (
                "This item was examined but intentionally skipped. Real ransomware often skips system files, "
                "its own output files, and unsupported formats to avoid breaking execution."
            ),
            "file_skipped": (
                "The file was not targeted. This usually means it was excluded by extension, path, permissions, "
                "or a rule designed to keep the simulation stable and realistic."
            ),
            "lock_start": (
                "The ransomware has started the locking step for this file. In a real attack this is the point "
                "where encryption or content replacement would begin to deny access to the victim."
            ),
            "locked_written": (
                "A locked placeholder or replacement file was written. This simulates the core damaging action "
                "of ransomware: making the victim's file unusable until recovery or decryption is possible."
            ),
            "original_hidden": (
                "The original file was hidden, renamed, or moved out of the normal workflow. This increases the "
                "perceived impact by making it appear that the victim's data has disappeared."
            ),
            "ransom_note_written": (
                "A ransom note was created to communicate attacker demands and instructions. This is part of the "
                "impact phase because it turns technical damage into visible extortion pressure."
            ),
            "impact_complete": (
                "The damage portion of the simulation has finished. At this stage the user would see locked files "
                "and ransom notes, which mirrors the visible end result of a real ransomware event."
            ),
            "summary": (
                "The simulation is finalizing and recording totals for review. This helps the user understand "
                "how many files were scanned, targeted, locked, skipped, or failed."
            ),
            "run_complete": (
                "The ransomware simulation has completed all of its planned actions and logged the final outcome "
                "for analysis and training purposes."
            ),
            "file_access_error": (
                "The ransomware attempted to access or modify a file but failed. Real malware encounters these "
                "issues because of permissions, locks from other processes, missing paths, or I/O problems."
            ),
            "db_write_error": (
                "A logging or database write failed. This does not necessarily stop ransomware behavior, but it "
                "reduces visibility into what actions were taken."
            ),
            "file_read_error": (
                "The simulation encountered an error while reading event data or a target file. This highlights "
                "an execution problem rather than a normal ransomware phase."
            ),
        }

        if event_type in descriptions:
            return descriptions[event_type]

        phase_fallbacks = {
            "INITIALIZATION": (
                "This event belongs to the startup phase, where the ransomware prepares its environment before "
                "touching files."
            ),
            "DISCOVERY": (
                "This event belongs to the discovery phase, where the ransomware maps the environment to find "
                "valuable user files and directories."
            ),
            "RECON": (
                "This event belongs to a reconnaissance phase, where the malware gathers information about the "
                "environment to improve targeting decisions."
            ),
            "COLLECTION": (
                "This event belongs to a collection phase, where the malware gathers file details needed to "
                "decide what should be processed."
            ),
            "TARGETING": (
                "This event belongs to the targeting phase, where the ransomware decides which files are worth "
                "locking and which should be skipped."
            ),
            "LOCKING": (
                "This event belongs to the locking phase, which represents the core compromise step where files "
                "are denied to the victim through replacement or simulated encryption."
            ),
            "IMPACT": (
                "This event belongs to the impact phase, where the victim begins to notice visible damage such "
                "as inaccessible files or ransom notes."
            ),
            "PERSISTENCE": (
                "This event belongs to a persistence-related phase, where malware attempts to remain active or "
                "recoverable inside the environment."
            ),
            "FINALIZATION": (
                "This event belongs to the finalization phase, where the ransomware wraps up execution and "
                "records results for review."
            ),
            "GENERAL": (
                "This event records general ransomware activity that does not clearly fall into one specialized "
                "phase."
            ),
        }

        if details:
            return phase_fallbacks.get(
                phase,
                "This log captures part of the ransomware workflow and explains a step the malware performed during execution."
            )

        return "This log captures part of the ransomware workflow during the simulation."

    def _event_to_row(self, ev: dict) -> dict:
        ts = self._format_ts(ev.get("ts", ""))
        event_type = ev.get("type", "event")
        severity = str(ev.get("severity", "INFO")).upper()
        phase = str(ev.get("phase", "GENERAL")).upper()
        message = ev.get("message", "")

        if event_type in ("summary", "run_complete"):
            stats = ev.get("stats", {})
            details = self._format_stats_details(stats)
        elif event_type == "target_selected":
            details = f"{ev.get('relative_path', '')} ({ev.get('extension', '')})"
        elif event_type == "locked_written":
            details = ev.get("locked_relative_path", "locked placeholder written")
        elif event_type == "lock_start":
            details = ev.get("locked_relative_path", "lock started")
        elif event_type == "ransom_note_written":
            details = ev.get("note_relative_path", "RANSOM_NOTE.txt written")
        elif event_type == "file_skipped":
            details = f"{ev.get('relative_path', '')} | reason={ev.get('reason', '')}"
        elif event_type == "metadata_collection_complete":
            details = f"{ev.get('relative_path', '')} | size={ev.get('size_bytes', '')}"
        else:
            details = message if message else json.dumps(
                {k: v for k, v in ev.items() if k != "ts"},
                ensure_ascii=False
            )

        description = self._build_description(phase, event_type, details, ev)

        return {
            "timestamp": ts,
            "severity": severity,
            "phase": phase,
            "event_type": event_type,
            "details": details,
            "description": description,
            "stats": ev.get("stats"),
        }

    def _db_event_to_row(self, db_record: dict) -> dict:
        raw_timestamp = db_record.get("timestamp", "")
        if isinstance(raw_timestamp, datetime):
            ts = raw_timestamp.strftime("%Y-%m-%d %H:%M:%S")
        else:
            ts = str(raw_timestamp)

        event_type = db_record.get("event_type", "event")

        try:
            details_dict = json.loads(db_record.get("details", "{}"))
        except Exception:
            details_dict = {}

        phase = str(details_dict.get("phase", "GENERAL")).upper()
        severity = str(details_dict.get("severity", "INFO")).upper()

        row = {
            "timestamp": ts,
            "severity": severity,
            "phase": phase,
            "event_type": event_type,
            "details": "",
            "description": "",
            "stats": details_dict.get("stats"),
        }

        if event_type in ("summary", "run_complete"):
            stats = details_dict.get("stats", {})
            row["details"] = self._format_stats_details(stats)
        elif event_type == "target_selected":
            row["details"] = f"{details_dict.get('relative_path', '')} ({details_dict.get('extension', '')})"
        elif event_type == "locked_written":
            row["details"] = details_dict.get("locked_relative_path", "locked placeholder written")
        elif event_type == "lock_start":
            row["details"] = details_dict.get("locked_relative_path", "lock started")
        elif event_type == "ransom_note_written":
            row["details"] = details_dict.get("note_relative_path", "RANSOM_NOTE.txt written")
        elif event_type == "file_skipped":
            row["details"] = f"{details_dict.get('relative_path', '')} | reason={details_dict.get('reason', '')}"
        elif event_type == "metadata_collection_complete":
            row["details"] = f"{details_dict.get('relative_path', '')} | size={details_dict.get('size_bytes', '')}"
        else:
            row["details"] = (
                details_dict.get("message")
                or db_record.get("file_path", "")
                or db_record.get("details", "")
            )

        row["description"] = self._build_description(phase, event_type, row["details"], details_dict)
        return row

    def _get_logs_from_db(self):
        if not self.db:
            return []

        if not hasattr(self.db, "connection") or not self.db.connection:
            return []

        try:
            if hasattr(self.db, "get_simulation_logs"):
                return self.db.get_simulation_logs(limit=1000)

            cursor = self.db.connection.cursor(dictionary=True)
            query = "SELECT * FROM simulation_logs ORDER BY timestamp DESC LIMIT 1000"
            cursor.execute(query)
            results = cursor.fetchall()
            cursor.close()
            return results

        except Exception as e:
            print(f"DB query error: {e}")
            return []

    def load_logs(self):
        self.all_logs.clear()

        db_logs = self._get_logs_from_db()
        if db_logs:
            self.all_logs = [self._db_event_to_row(log) for log in db_logs]

        if not self.all_logs:
            events_path = self._events_path()
            if events_path.exists():
                try:
                    with events_path.open("r", encoding="utf-8") as f:
                        for line in f:
                            line = line.strip()
                            if not line:
                                continue
                            ev = json.loads(line)
                            self.all_logs.append(self._event_to_row(ev))
                except Exception as e:
                    self.all_logs = [{
                        "timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
                        "severity": "ERROR",
                        "phase": "GENERAL",
                        "event_type": "file_read_error",
                        "details": f"Failed to read {events_path}: {e}",
                        "description": "The logs page could not read the event stream file, so the simulation output could not be loaded from disk.",
                        "stats": None,
                    }]

        if not self.all_logs:
            self.all_logs = list(self.demo_logs)

        types = sorted({row["event_type"] for row in self.all_logs})
        self.filter_combo["values"] = ["All Events"] + types
        if self.filter_var.get() not in self.filter_combo["values"]:
            self.filter_var.set("All Events")

        self._update_summary_label()
        self.filter_logs()
        self._clear_details_panel()

    def _update_summary_label(self):
        summary_row = None
        for row in reversed(self.all_logs):
            if row["event_type"] in ("summary", "run_complete") and row.get("stats"):
                summary_row = row
                break

        if summary_row and summary_row.get("stats"):
            stats = summary_row["stats"]
            self.summary_label.config(
                text=(
                    f"Summary: scanned={stats.get('files_scanned', 0)} | "
                    f"skipped={stats.get('files_skipped', 0)} | "
                    f"targeted={stats.get('files_targeted', 0)} | "
                    f"locked={stats.get('files_locked', 0)} | "
                    f"notes={stats.get('notes_written', 0)} | "
                    f"errors={stats.get('errors', 0)}"
                )
            )
        else:
            self.summary_label.config(text="Summary: none available")

    def _phase_tag(self, phase: str, severity: str) -> str:
        phase = str(phase or "GENERAL").upper()
        severity = str(severity or "INFO").upper()

        if severity == "ERROR":
            return "ERROR_PHASE"

        allowed = {
            "INITIALIZATION",
            "DISCOVERY",
            "RECON",
            "COLLECTION",
            "TARGETING",
            "LOCKING",
            "IMPACT",
            "PERSISTENCE",
            "FINALIZATION",
            "GENERAL",
        }

        if phase in allowed:
            return phase

        return "DEFAULT"

    def filter_logs(self, event=None):
        for item in self.tree.get_children():
            self.tree.delete(item)

        filter_type = self.filter_var.get()
        search_text = (self.search_entry.get() or "").lower().strip()

        for row in self.all_logs:
            if filter_type != "All Events" and row["event_type"] != filter_type:
                continue

            combined = (
                f"{row['timestamp']} {row['severity']} {row['phase']} "
                f"{row['event_type']} {row['details']} {row.get('description', '')}"
            ).lower()

            if search_text and search_text not in combined:
                continue

            phase_tag = self._phase_tag(row["phase"], row["severity"])

            self.tree.insert(
                "",
                tk.END,
                values=(
                    row["timestamp"],
                    row["severity"],
                    row["phase"],
                    row["event_type"],
                    row["details"],
                    row.get("description", ""),
                ),
                tags=(phase_tag,),
            )

        self._clear_details_panel()

    def _clear_details_panel(self):
        if not self.details_text:
            return

        self.details_text.config(state=tk.NORMAL)
        self.details_text.delete("1.0", tk.END)
        self.details_text.insert(
            "1.0",
            "Select a log row above to view the full Details and Description text."
        )
        self.details_text.config(state=tk.DISABLED)

    def on_row_select(self, event=None):
        selected = self.tree.selection()
        if not selected or not self.details_text:
            return

        item = self.tree.item(selected[0])
        values = item.get("values", [])

        if len(values) < 6:
            return

        timestamp, severity, phase, event_type, details, description = values

        full_text = (
            f"Timestamp: {timestamp}\n"
            f"Severity: {severity}\n"
            f"Phase: {phase}\n"
            f"Event Type: {event_type}\n\n"
            f"Details:\n{details}\n\n"
            f"Description:\n{description}"
        )

        self.details_text.config(state=tk.NORMAL)
        self.details_text.delete("1.0", tk.END)
        self.details_text.insert("1.0", full_text)
        self.details_text.config(state=tk.DISABLED)

    def export_logs(self):
        file_path = filedialog.asksaveasfilename(
            defaultextension=".csv",
            filetypes=[
                ("CSV files", "*.csv"),
                ("Text files", "*.txt"),
                ("All files", "*.*"),
            ],
            title="Export Logs",
        )

        if not file_path:
            return

        try:
            with open(file_path, "w", newline="", encoding="utf-8") as f:
                writer = csv.writer(f)
                writer.writerow(["Timestamp", "Severity", "Phase", "Event Type", "Details", "Description"])
                for item in self.tree.get_children():
                    writer.writerow(self.tree.item(item, "values"))
            messagebox.showinfo("Success", f"Logs exported successfully to {Path(file_path).name}")
        except Exception as e:
            messagebox.showerror("Error", f"Failed to export logs: {str(e)}")

    def go_back(self):
        self.on_back()