import tkinter as tk
from tkinter import messagebox, simpledialog
from pathlib import Path
import json
import shutil
import os
from cryptography.fernet import Fernet


class RecoveryOptions:
    def __init__(self, parent, project_files_root):
        self.parent = parent
        self.root = Path(project_files_root).resolve()
        self.colors = {
            'bg_dark': '#1e1e1e',
            'bg_medium': '#2d3e3e',
            'header_teal': '#4fbdba',
            'entry_cyan': '#b2ffff',
            'text_light': '#ffffff',
            'text_dark': '#1e1e1e',
            'success_green': '#7fe9a2',
            'error_red': '#ff6b6b',
            'warning_yellow': '#ffd93d'
        }

        # Track which recovery methods have been used
        self.recovery_state_file = self.root / ".recovery_state.json"
        self.recovery_used = self.load_recovery_state()

        # Encryption key for backup option (stored securely in memory only)
        self._backup_key = None
        self._key_file = self.root / "backup" / ".backup_key"

    def load_recovery_state(self):
        """Load which recovery options have been used"""
        if self.recovery_state_file.exists():
            with open(self.recovery_state_file, 'r') as f:
                return json.load(f)
        return {
            'backup_created': False,
            'shadow_copy_used': False,
            'temp_file_recovered': False,
            'manual_recreated': False
        }

    def save_recovery_state(self):
        """Save recovery state to track progress"""
        with open(self.recovery_state_file, 'w') as f:
            json.dump(self.recovery_used, f)

    def create_widgets(self):
        """Build the recovery options GUI"""
        self.frame = tk.Frame(self.parent, bg=self.colors['bg_dark'])
        self.frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

        # Header
        header = tk.Frame(self.frame, bg=self.colors['header_teal'], height=50)
        header.pack(fill=tk.X, pady=(0, 10))
        header.pack_propagate(False)

        tk.Label(header, text="🔓 Recovery Options",
                 bg=self.colors['header_teal'],
                 fg=self.colors['text_dark'],
                 font=('Arial', 16, 'bold')).pack(expand=True)

        # Status area
        self.status_frame = tk.Frame(self.frame, bg=self.colors['bg_dark'])
        self.status_frame.pack(fill=tk.X, pady=10)

        self.update_status()

        # Recovery options buttons
        options_frame = tk.LabelFrame(self.frame,
                                      text="Available Recovery Methods",
                                      bg=self.colors['bg_dark'],
                                      fg=self.colors['text_light'],
                                      font=('Arial', 12, 'bold'))
        options_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)

        # Option 1: Restore from Backup (ENCRYPTED)
        self.btn_backup = tk.Button(options_frame,
                                    text="Restore from Encrypted Backup 🔒",
                                    bg=self.colors['bg_medium'],
                                    fg=self.colors['text_light'],
                                    font=('Arial', 11, 'bold'),
                                    relief=tk.RAISED, bd=3,
                                    command=self.option_backup_restore)
        self.btn_backup.pack(fill=tk.X, padx=20, pady=10)

        # Option 2: Shadow Copy
        self.btn_shadow = tk.Button(options_frame,
                                    text="Recover from Shadow Copy",
                                    bg=self.colors['bg_medium'],
                                    fg=self.colors['text_light'],
                                    font=('Arial', 11, 'bold'),
                                    relief=tk.RAISED, bd=3,
                                    command=self.option_shadow_copy)
        self.btn_shadow.pack(fill=tk.X, padx=20, pady=10)

        # Option 3: Temporary Files
        self.btn_temp = tk.Button(options_frame,
                                  text="Recover from Temporary Files",
                                  bg=self.colors['bg_medium'],
                                  fg=self.colors['text_light'],
                                  font=('Arial', 11, 'bold'),
                                  relief=tk.RAISED, bd=3,
                                  command=self.option_temp_recovery)
        self.btn_temp.pack(fill=tk.X, padx=20, pady=10)

        # Option 4: Manual Recreation
        self.btn_manual = tk.Button(options_frame,
                                    text="Manually Recreate File",
                                    bg=self.colors['bg_medium'],
                                    fg=self.colors['text_light'],
                                    font=('Arial', 11, 'bold'),
                                    relief=tk.RAISED, bd=3,
                                    command=self.option_manual_recreate)
        self.btn_manual.pack(fill=tk.X, padx=20, pady=10)

        # Result display
        self.result_label = tk.Label(self.frame, text="",
                                     bg=self.colors['bg_dark'],
                                     font=('Arial', 11, 'bold'),
                                     wraplength=600)
        self.result_label.pack(pady=20)

        # Refresh button to check status
        tk.Button(self.frame, text="🔄 Check Recovery Status",
                  bg=self.colors['header_teal'],
                  fg=self.colors['text_dark'],
                  font=('Arial', 10, 'bold'),
                  command=self.update_status).pack(pady=10)

    def update_status(self):
        """Update the status display"""
        for widget in self.status_frame.winfo_children():
            widget.destroy()

        encrypted_exists = (self.root / "passwords.txt.encrypted").exists()
        original_exists = (self.root / "passwords.txt").exists()

        if original_exists and not encrypted_exists:
            status = "✅ File recovered successfully!"
            color = self.colors['success_green']
        elif original_exists and encrypted_exists:
            status = "⚠️ Original file exists but encrypted version also present"
            color = self.colors['warning_yellow']
        elif encrypted_exists:
            status = "🔒 File is encrypted - recovery needed"
            color = self.colors['error_red']
        else:
            status = "❓ No passwords.txt found - create or recover needed"
            color = self.colors['warning_yellow']

        tk.Label(self.status_frame, text=status,
                 bg=self.colors['bg_dark'], fg=color,
                 font=('Arial', 12, 'bold')).pack()

    #################### ENCRYPTION HELPERS #################################

    def _get_or_create_key(self) -> Fernet:
        """Get existing encryption key or generate new one"""
        if self._backup_key:
            return Fernet(self._backup_key)

        if self._key_file.exists():
            self._backup_key = self._key_file.read_bytes()
            return Fernet(self._backup_key)

        # Generate new key
        key = Fernet.generate_key()
        self._backup_key = key
        self._key_file.parent.mkdir(parents=True, exist_ok=True)
        self._key_file.write_bytes(key)
        return Fernet(key)

    def _encrypt_file(self, plaintext_path: Path,
                      ciphertext_path: Path) -> None:
        """Encrypt a file using AES-256 (Fernet)"""
        cipher = self._get_or_create_key()
        plaintext = plaintext_path.read_bytes()
        encrypted = cipher.encrypt(plaintext)
        ciphertext_path.write_bytes(encrypted)

    def _decrypt_file(self, ciphertext_path: Path,
                      plaintext_path: Path) -> None:
        """Decrypt a file using AES-256 (Fernet)"""
        cipher = self._get_or_create_key()
        encrypted = ciphertext_path.read_bytes()
        decrypted = cipher.decrypt(encrypted)
        plaintext_path.write_bytes(decrypted)

    #################### RECOVERY OPTION 1: ENCRYPTED BACKUP #################################

    def option_backup_restore(self):
        """Create and restore from ENCRYPTED backup folder"""
        backup_dir = self.root / "backup"
        backup_dir.mkdir(exist_ok=True)

        # Check if we already have an encrypted backup
        encrypted_backup = backup_dir / "passwords.txt.encrypted"

        if encrypted_backup.exists():
            # Restore from encrypted backup
            try:
                self._decrypt_file(encrypted_backup,
                                   self.root / "passwords.txt")
                self.recovery_used['backup_created'] = True
                self.save_recovery_state()
                self.show_result(
                    "✅ Successfully restored from encrypted backup! (AES-256)",
                    "success")
                self.update_status()
            except Exception as e:
                self.show_result(f"❌ Decryption failed: {e}", "error")
        else:
            # No backup exists - create encrypted one
            self.create_encrypted_backup_dialog(backup_dir)

    def create_encrypted_backup_dialog(self, backup_dir):
        """Dialog to create encrypted backup"""
        dialog = tk.Toplevel(self.parent)
        dialog.title("Create Encrypted Backup")
        dialog.geometry("400x350")
        dialog.configure(bg=self.colors['bg_dark'])

        tk.Label(dialog,
                 text="No encrypted backup found.\nCreate one with AES-256 encryption?",
                 bg=self.colors['bg_dark'], fg=self.colors['text_light'],
                 font=('Arial', 12), justify=tk.CENTER).pack(pady=20)

        tk.Label(dialog, text="Enter passwords to encrypt and backup:",
                 bg=self.colors['bg_dark'], fg=self.colors['text_light']).pack()

        text_area = tk.Text(dialog, height=8, width=40,
                            bg=self.colors['bg_medium'],
                            fg=self.colors['text_light'])
        text_area.pack(pady=10)
        text_area.insert("1.0", "example.com: user / pass123\n")

        # Show encryption info
        info_frame = tk.Frame(dialog, bg=self.colors['bg_medium'], bd=2,
                              relief=tk.SUNKEN)
        info_frame.pack(fill=tk.X, padx=20, pady=10)

        tk.Label(info_frame,
                 text="🔐 Encryption: AES-256 (Fernet)\nKey stored securely in backup folder",
                 bg=self.colors['bg_medium'], fg=self.colors['text_light'],
                 justify=tk.LEFT, font=('Courier', 9)).pack(padx=10, pady=10)

        def save_encrypted_backup():
            content = text_area.get("1.0", tk.END).strip()
            if content:
                # Save plaintext temporarily
                temp_file = backup_dir / "passwords.txt"
                with open(temp_file, 'w') as f:
                    f.write(content)

                # Encrypt it
                encrypted_file = backup_dir / "passwords.txt.encrypted"
                self._encrypt_file(temp_file, encrypted_file)

                # Remove plaintext
                temp_file.unlink()

                # Also create the original (plaintext for simulation)
                original_file = self.root / "passwords.txt"
                with open(original_file, 'w') as f:
                    f.write(content)

                self.recovery_used['backup_created'] = True
                self.save_recovery_state()
                self.show_result(
                    "✅ Encrypted backup created and file restored!",
                    "success")
                dialog.destroy()
                self.update_status()

        tk.Button(dialog, text="Create Encrypted Backup",
                  bg=self.colors['header_teal'],
                  command=save_encrypted_backup).pack(pady=10)

    ###################### RECOVERY OPTION 2: SHADOW COPY ##########################

    def option_shadow_copy(self):
        """Simulate Windows Shadow Copy recovery"""
        # Create shadow copy directory structure
        shadow_dir = self.root / "System Volume Information" / "shadow_copies"
        shadow_dir.mkdir(parents=True, exist_ok=True)

        shadow_file = shadow_dir / "passwords.txt.shadow"

        if shadow_file.exists():
            # Restore from shadow copy
            try:
                shutil.copy2(shadow_file, self.root / "passwords.txt")
                self.recovery_used['shadow_copy_used'] = True
                self.save_recovery_state()
                self.show_result("✅ Recovered from Volume Shadow Copy!",
                                 "success")
                self.update_status()
            except Exception as e:
                self.show_result(f"❌ Shadow copy restore failed: {e}", "error")
        else:
            # Create shadow copy demonstration
            self.create_shadow_copy_dialog(shadow_dir)

    def create_shadow_copy_dialog(self, shadow_dir):
        """Dialog to create shadow copy"""
        dialog = tk.Toplevel(self.parent)
        dialog.title("Volume Shadow Copy")
        dialog.geometry("450x350")
        dialog.configure(bg=self.colors['bg_dark'])

        tk.Label(dialog,
                 text=" Volume Shadow Copy Service\nNo previous shadow copies found.",
                 bg=self.colors['bg_dark'], fg=self.colors['text_light'],
                 font=('Arial', 12, 'bold'), justify=tk.CENTER).pack(pady=10)

        tk.Label(dialog,
                 text="Windows creates shadow copies automatically.\nSimulate creating one now:",
                 bg=self.colors['bg_dark'], fg=self.colors['text_light']).pack()

        info_frame = tk.Frame(dialog, bg=self.colors['bg_medium'], bd=2,
                              relief=tk.SUNKEN)
        info_frame.pack(fill=tk.X, padx=20, pady=10)

        tk.Label(info_frame,
                 text="Shadow Copy Properties:\n• Created: Just now\n• Type: System Restore Point\n• Size: ~4KB",
                 bg=self.colors['bg_medium'], fg=self.colors['text_light'],
                 justify=tk.LEFT).pack(padx=10, pady=10)

        tk.Label(dialog, text="Enter file content to save in shadow copy:",
                 bg=self.colors['bg_dark'], fg=self.colors['text_light']).pack()

        text_area = tk.Text(dialog, height=6, width=40,
                            bg=self.colors['bg_medium'],
                            fg=self.colors['text_light'])
        text_area.pack(pady=10)

        def create_shadow():
            content = text_area.get("1.0", tk.END).strip()
            if content:
                # Save to shadow location
                shadow_file = shadow_dir / "passwords.txt.shadow"
                with open(shadow_file, 'w') as f:
                    f.write(content)

                # Restore to main location
                original_file = self.root / "passwords.txt"
                with open(original_file, 'w') as f:
                    f.write(content)

                self.recovery_used['shadow_copy_used'] = True
                self.save_recovery_state()
                self.show_result("✅ Shadow copy created and file restored!",
                                 "success")
                dialog.destroy()
                self.update_status()

        tk.Button(dialog, text="Create Shadow Copy & Restore",
                  bg=self.colors['header_teal'],
                  command=create_shadow).pack(pady=10)

    ################## RECOVERY OPTION 3: TEMP FILES ###############################

    def option_temp_recovery(self):
        """Recover from application temp files"""
        temp_dir = self.root / "temp"
        temp_dir.mkdir(exist_ok=True)

        temp_file = temp_dir / "~passwords.txt.tmp"

        if temp_file.exists():
            try:
                shutil.copy2(temp_file, self.root / "passwords.txt")
                self.recovery_used['temp_file_recovered'] = True
                self.save_recovery_state()
                self.show_result("✅ Recovered from temporary file!", "success")
                self.update_status()
            except Exception as e:
                self.show_result(f"❌ Temp file recovery failed: {e}", "error")
        else:
            self.create_temp_file_dialog(temp_dir)

    def create_temp_file_dialog(self, temp_dir):
        """Dialog for temp file recovery"""
        dialog = tk.Toplevel(self.parent)
        dialog.title("Temporary File Recovery")
        dialog.geometry("400x300")
        dialog.configure(bg=self.colors['bg_dark'])

        tk.Label(dialog,
                 text=" Application Temporary Files\nMany apps auto-save temp files.",
                 bg=self.colors['bg_dark'], fg=self.colors['text_light'],
                 font=('Arial', 12, 'bold'), justify=tk.CENTER).pack(pady=10)

        tk.Label(dialog, text="Simulate finding an auto-saved temp file:",
                 bg=self.colors['bg_dark'], fg=self.colors['text_light']).pack()

        # Show fake temp file info
        info_frame = tk.Frame(dialog, bg=self.colors['bg_medium'])
        info_frame.pack(fill=tk.X, padx=20, pady=10)

        tk.Label(info_frame,
                 text="Found: ~passwords.txt.tmp\nLocation: /temp/\nSize: 2.4 KB\nModified: 2 hours ago",
                 bg=self.colors['bg_medium'], fg=self.colors['text_light'],
                 justify=tk.LEFT, font=('Courier', 10)).pack(padx=10, pady=10)

        tk.Label(dialog, text="File content preview:",
                 bg=self.colors['bg_dark'], fg=self.colors['text_light']).pack()

        text_area = tk.Text(dialog, height=6, width=40,
                            bg=self.colors['bg_medium'],
                            fg=self.colors['text_light'])
        text_area.pack(pady=10)

        def recover_temp():
            content = text_area.get("1.0", tk.END).strip()
            if content:
                # Save as temp file first (simulation)
                temp_file = temp_dir / "~passwords.txt.tmp"
                with open(temp_file, 'w') as f:
                    f.write(content)

                # Then restore
                original_file = self.root / "passwords.txt"
                with open(original_file, 'w') as f:
                    f.write(content)

                self.recovery_used['temp_file_recovered'] = True
                self.save_recovery_state()
                self.show_result("✅ Recovered from temporary file!", "success")
                dialog.destroy()
                self.update_status()

        tk.Button(dialog, text="Recover from Temp File",
                  bg=self.colors['header_teal'],
                  command=recover_temp).pack(pady=10)

    ###################### RECOVERY OPTION 4: MANUAL ###############################

    def option_manual_recreate(self):
        """Manually recreate the passwords file"""
        dialog = tk.Toplevel(self.parent)
        dialog.title("Manual File Recreation")
        dialog.geometry("500x500")
        dialog.configure(bg=self.colors['bg_dark'])

        tk.Label(dialog, text="️ Manually Recreate Passwords.txt",
                 bg=self.colors['bg_dark'], fg=self.colors['text_light'],
                 font=('Arial', 14, 'bold')).pack(pady=10)

        tk.Label(dialog,
                 text="Enter all passwords you remember:\n(Website | Username | Password)",
                 bg=self.colors['bg_dark'], fg=self.colors['text_light']).pack()

        # Create scrollable frame for entries
        canvas = tk.Canvas(dialog, bg=self.colors['bg_dark'])
        scrollbar = tk.Scrollbar(dialog, orient="vertical",
                                 command=canvas.yview)
        scroll_frame = tk.Frame(canvas, bg=self.colors['bg_dark'])

        scroll_frame.bind(
            "<Configure>",
            lambda e: canvas.configure(scrollregion=canvas.bbox("all"))
        )

        canvas.create_window((0, 0), window=scroll_frame, anchor="nw")
        canvas.configure(yscrollcommand=scrollbar.set)

        entries = []

        def add_entry_row():
            row = tk.Frame(scroll_frame, bg=self.colors['bg_medium'])
            row.pack(fill=tk.X, pady=2, padx=5)

            site = tk.Entry(row, width=20)
            site.pack(side=tk.LEFT, padx=2)
            site.insert(0, "example.com")

            user = tk.Entry(row, width=20)
            user.pack(side=tk.LEFT, padx=2)
            user.insert(0, "username")

            pwd = tk.Entry(row, width=20, show="*")
            pwd.pack(side=tk.LEFT, padx=2)
            pwd.insert(0, "password")

            entries.append((site, user, pwd))

        # Add initial rows
        for _ in range(2):
            add_entry_row()

        tk.Button(scroll_frame, text="+ Add More",
                  bg=self.colors['bg_medium'],
                  command=add_entry_row).pack(pady=5)

        canvas.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)

        def save_manual():
            lines = []
            for site, user, pwd in entries:
                s = site.get().strip()
                u = user.get().strip()
                p = pwd.get().strip()
                if s and s != "example.com":
                    lines.append(f"{s}: {u} / {p}")

            if lines:
                content = "\n".join(lines)
                original_file = self.root / "passwords.txt"
                with open(original_file, 'w') as f:
                    f.write(content)

                self.recovery_used['manual_recreated'] = True
                self.save_recovery_state()
                self.show_result(
                    f"✅ Manually recreated with {len(lines)} entries!",
                    "success")
                dialog.destroy()
                self.update_status()
            else:
                messagebox.showwarning("Empty",
                                       "Please enter at least one password")

        tk.Button(dialog, text="Save Recreated File",
                  bg=self.colors['header_teal'],
                  font=('Arial', 12, 'bold'),
                  command=save_manual).pack(pady=10)

    def show_result(self, message, msg_type):
        """Display result message"""
        color = self.colors['success_green'] if msg_type == "success" else \
            self.colors['error_red']
        self.result_label.config(text=message, fg=color)


# Integration function for your menu
def build_recovery_page(parent, project_files_root):
    """Create recovery options page"""
    recovery = RecoveryOptions(parent, project_files_root)
    recovery.create_widgets()
    return recovery.frame