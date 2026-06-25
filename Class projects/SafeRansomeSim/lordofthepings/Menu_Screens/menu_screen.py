import tkinter as tk
from pathlib import Path
from tkinter import ttk, messagebox, filedialog
import csv
import os
import sys
import subprocess
from profilePage import ProfilePage
from ransomware_simulator import run_simulation
import threading
from logs_page import LogsPage as LogsPageExternal
from Recovery_Options import build_recovery_page
from incident_response_tools import IncidentResponsePage


class LogsPage:
    def __init__(self, parent, on_back=None):
        self.parent = parent
        self.on_back = on_back or (lambda: None)
        self.tree = None
        self.search_entry = None
        self.filter_combo = None
        self.filter_var = None
        self.colors = {
            'bg_dark': '#1e1e1e',
            'bg_medium': '#2d3e3e',
            'bg_light': '#3a4a4a',
            'header_teal': '#4fbdba',
            'text_light': '#ffffff',
            'text_dark': '#1e1e1e',
            'accent': '#5f6f6f',
            'row_alt': '#354545'
        }
        self.all_logs = [
            ("10:13:01 11/08/25", "START",
             "simulation started, ransomware started"),
            ("10:13:05 11/08/25", "ENCRYPTED", "image.jpg encrypted"),
            ("10:13:11 11/08/25", "ENCRYPTED", "example.docx encrypted"),
            ("10:13:21 11/08/25", "KEY ENTERED", "incorrect key entered"),
            ("10:13:24 11/08/25", "KEY ENTERED", "correct key entered"),
            ("10:13:32 11/08/25", "RESTORE", "backup successfully run"),
        ]
        self.setup_styles()
        self.create_widgets()
        self.load_logs()

    def setup_styles(self):
        style = ttk.Style()
        style.theme_use('default')
        style.configure("Custom.Treeview",
                        background=self.colors['bg_medium'],
                        foreground=self.colors['text_light'],
                        fieldbackground=self.colors['bg_medium'],
                        rowheight=25)
        style.configure("Custom.Treeview.Heading",
                        background=self.colors['bg_light'],
                        foreground=self.colors['text_light'],
                        font=('Arial', 10, 'bold'))
        style.map("Custom.Treeview",
                  background=[('selected', self.colors['header_teal'])])
        style.configure("Custom.TCombobox",
                        fieldbackground=self.colors['bg_medium'],
                        background=self.colors['bg_light'],
                        foreground=self.colors['text_light'])

    def create_widgets(self):
        main_frame = tk.Frame(self.parent, bg=self.colors['bg_dark'])
        main_frame.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        title_frame = tk.Frame(main_frame, bg=self.colors['header_teal'],
                               height=40)
        title_frame.pack(fill=tk.X, pady=(0, 10))
        title_frame.pack_propagate(False)
        title_label = tk.Label(
            title_frame,
            text="Logs Page",
            bg=self.colors['header_teal'],
            fg=self.colors['text_dark'],
            font=('Arial', 16, 'bold', 'italic')
        )
        title_label.pack(expand=True)
        controls_frame = tk.Frame(main_frame, bg=self.colors['bg_dark'])
        controls_frame.pack(fill=tk.X, pady=(0, 10))
        tk.Label(
            controls_frame, text="Filter:",
            bg=self.colors['bg_dark'], fg=self.colors['text_light'],
            font=('Arial', 10, 'bold')
        ).pack(side=tk.LEFT, padx=(0, 5))
        self.filter_var = tk.StringVar(value="All Events")
        self.filter_combo = ttk.Combobox(
            controls_frame,
            textvariable=self.filter_var,
            values=["All Events", "START", "ENCRYPTED", "KEY ENTERED",
                    "RESTORE"],
            state="readonly",
            width=15,
            style="Custom.TCombobox"
        )
        self.filter_combo.pack(side=tk.LEFT, padx=(0, 20))
        self.filter_combo.bind('<<ComboboxSelected>>', self.filter_logs)
        tk.Label(
            controls_frame, text="Search:",
            bg=self.colors['bg_dark'], fg=self.colors['text_light'],
            font=('Arial', 10, 'bold')
        ).pack(side=tk.LEFT, padx=(0, 5))
        self.search_entry = tk.Entry(
            controls_frame,
            bg=self.colors['bg_medium'],
            fg=self.colors['text_light'],
            insertbackground=self.colors['text_light'],
            relief=tk.SUNKEN,
            bd=2,
            font=('Arial', 10)
        )
        self.search_entry.pack(side=tk.LEFT, fill=tk.X, expand=True,
                               padx=(0, 5))
        self.search_entry.bind('<KeyRelease>', self.filter_logs)
        tk.Button(
            controls_frame,
            text="🔍",
            bg=self.colors['bg_medium'],
            fg=self.colors['text_light'],
            relief=tk.RAISED,
            bd=2,
            command=self.filter_logs
        ).pack(side=tk.LEFT)
        tree_frame = tk.Frame(main_frame, bg=self.colors['bg_dark'])
        tree_frame.pack(fill=tk.BOTH, expand=True, pady=(0, 10))
        vsb = ttk.Scrollbar(tree_frame, orient="vertical")
        vsb.pack(side=tk.RIGHT, fill=tk.Y)
        columns = ('timestamp', 'event_type', 'details')
        self.tree = ttk.Treeview(
            tree_frame,
            columns=columns,
            show='headings',
            style="Custom.Treeview",
            yscrollcommand=vsb.set
        )
        self.tree.pack(fill=tk.BOTH, expand=True)
        vsb.config(command=self.tree.yview)
        self.tree.heading('timestamp', text='Timestamp')
        self.tree.heading('event_type', text='Event Type')
        self.tree.heading('details', text='Details')
        self.tree.column('timestamp', width=150, anchor='w')
        self.tree.column('event_type', width=120, anchor='w')
        self.tree.column('details', width=400, anchor='w')
        self.tree.tag_configure('oddrow', background=self.colors['bg_medium'])
        self.tree.tag_configure('evenrow', background=self.colors['row_alt'])
        self.tree.tag_configure('START', foreground='#7fe9a2')
        self.tree.tag_configure('ENCRYPTED', foreground='#ff6b6b')
        self.tree.tag_configure('KEY_ENTERED', foreground='#ffd93d')
        self.tree.tag_configure('RESTORE', foreground='#4fbdba')
        bottom_frame = tk.Frame(main_frame, bg=self.colors['bg_dark'])
        bottom_frame.pack(fill=tk.X)
        tk.Button(
            bottom_frame,
            text="export logs",
            bg=self.colors['bg_medium'],
            fg=self.colors['text_light'],
            font=('Arial', 10, 'bold'),
            relief=tk.RAISED,
            bd=3,
            padx=20,
            pady=5,
            command=self.export_logs
        ).pack(side=tk.LEFT)
        tk.Button(
            bottom_frame,
            text="Back",
            bg=self.colors['bg_medium'],
            fg=self.colors['text_light'],
            font=('Arial', 10, 'bold'),
            relief=tk.RAISED,
            bd=3,
            padx=20,
            pady=5,
            command=self.go_back
        ).pack(side=tk.RIGHT)

    def load_logs(self):
        self.filter_logs()

    def filter_logs(self, event=None):
        for item in self.tree.get_children():
            self.tree.delete(item)
        filter_type = self.filter_var.get()
        search_text = self.search_entry.get().lower()
        for idx, (timestamp, event_type, details) in enumerate(self.all_logs):
            if filter_type != "All Events" and event_type != filter_type:
                continue
            if search_text and (search_text not in timestamp.lower()
                                and search_text not in event_type.lower()
                                and search_text not in details.lower()):
                continue
            row_tag = 'evenrow' if idx % 2 == 0 else 'oddrow'
            event_tag = ''
            if event_type == "START":
                event_tag = 'START'
            elif event_type == "ENCRYPTED":
                event_tag = 'ENCRYPTED'
            elif event_type == "KEY ENTERED":
                event_tag = 'KEY_ENTERED'
            elif event_type == "RESTORE":
                event_tag = 'RESTORE'
            self.tree.insert('', tk.END,
                             values=(timestamp, event_type, details),
                             tags=(row_tag, event_tag))

    def export_logs(self):
        file_path = filedialog.asksaveasfilename(
            defaultextension=".csv",
            filetypes=[("CSV files", "*.csv"), ("Text files", "*.txt"),
                       ("All files", "*.*")],
            title="Export Logs"
        )
        if not file_path:
            return
        try:
            with open(file_path, 'w', newline='', encoding='utf-8') as f:
                writer = csv.writer(f)
                writer.writerow(['Timestamp', 'Event Type', 'Details'])
                for item in self.tree.get_children():
                    writer.writerow(self.tree.item(item, 'values'))
            messagebox.showinfo(
                "Success",
                f"Logs exported successfully to {os.path.basename(file_path)}"
            )
        except Exception as e:
            messagebox.showerror("Error", f"Failed to export logs: {str(e)}")

    def go_back(self):
        self.on_back()


def create_pre_page(notebook, controller, user_id, username, db_manager):
    page_pre = tk.Frame(notebook, bg="#2e2e2e")
    header1 = tk.Frame(page_pre, bg="#008080", height=60)
    header1.pack(fill="x")
    tk.Label(header1, text="Menu Screen", font=("Arial", 20, "bold italic"),
             bg="#008080", fg="white").pack(pady=10)
    main_area1 = tk.Frame(page_pre)
    main_area1.pack(fill="both", expand=True)
    left_panel1 = tk.Frame(main_area1, bg="#d3d3d3", width=250)
    left_panel1.pack(side="left", fill="y")
    right_panel1 = tk.Frame(main_area1, bg="#2e2e2e")
    right_panel1.pack(side="left", fill="both", expand=True)

    def build_run_simulation(parent):
        for w in parent.winfo_children():
            w.destroy()

        container = tk.Frame(parent, bg="#2e2e2e")
        container.pack(fill="both", expand=True)

        center_frame = tk.Frame(container, bg="#2e2e2e")
        center_frame.place(relx=0.5, rely=0.5, anchor="center")

        tk.Label(
            center_frame,
            text="Safe Ransomware Simulation",
            font=("Arial", 22, "bold"),
            bg="#2e2e2e",
            fg="#4fbdba"
        ).pack(pady=(0, 25))

        status_var = tk.StringVar(value="Ready")
        tk.Label(
            center_frame,
            textvariable=status_var,
            font=("Arial", 12),
            bg="#2e2e2e",
            fg="white"
        ).pack(pady=(0, 20))

        project_root = Path(__file__).resolve().parent.parent
        project_files_root = (project_root / "project_files").resolve()

        def do_run():
            try:
                status_var.set("Running simulation...")
                out = run_simulation(
                    project_files_root,
                    db_manager=db_manager,
                    user_id=user_id
                )
                status_var.set(f"Simulation complete: {out.name}")
            except Exception as e:
                status_var.set(f"Error: {e}")

        def start_run():
            threading.Thread(target=do_run, daemon=True).start()

        tk.Button(
            center_frame,
            text="Run Simulation",
            font=("Arial", 16, "bold"),
            bg="#4fbdba",
            fg="#1e1e1e",
            activebackground="#66d1cd",
            activeforeground="#1e1e1e",
            padx=40,
            pady=15,
            relief="flat",
            bd=0,
            cursor="hand2",
            command=start_run
        ).pack()

    def show_in_right_panel(build_fn):
        for w in right_panel1.winfo_children():
            w.destroy()
        build_fn(right_panel1)

    def build_placeholder(parent, title: str):
        for w in parent.winfo_children():
            w.destroy()
        tk.Label(parent, text=f" Welcome to SafeRansomSim \n Select a menu option from the left to continue",
                 font=("Arial", 16, "bold"), bg="#2e2e2e", fg="white").pack(
            anchor="nw", padx=20, pady=20)

    def build_file_manager(parent):
        project_root = Path(__file__).resolve().parent.parent
        root_dir = (project_root / "project_files").resolve()
        root_dir.mkdir(parents=True, exist_ok=True)
        current_dir = root_dir
        container = tk.Frame(parent, bg="#2e2e2e")
        container.pack(fill="both", expand=True)
        top = tk.Frame(container, bg="#2e2e2e")
        top.pack(fill="x", padx=10, pady=10)
        path_var = tk.StringVar(value=str(current_dir))
        path_entry = tk.Entry(top, textvariable=path_var)
        path_entry.pack(side="left", fill="x", expand=True, padx=(0, 8))
        path_entry.configure(state="readonly")

        def is_inside_root(p: Path) -> bool:
            try:
                p = p.resolve()
                r = root_dir.resolve()

                if not p.exists():
                    return False

                if '..' in str(p):
                    return False

                return p == r or r in p.parents
            except Exception:
                return False

        def open_with_default_app(path: Path):
            try:
                if sys.platform.startswith("win"):
                    os.startfile(str(path))
                elif sys.platform == "darwin":
                    subprocess.run(["open", str(path)], check=False)
                else:
                    subprocess.run(["xdg-open", str(path)], check=False)
            except Exception as e:
                messagebox.showerror("Open failed",
                                     f"Could not open:\n{path}\n\n{e}")

        def go_to(path: Path):
            nonlocal current_dir

            if len(str(path)) > 500:
                messagebox.showerror("Error", "Path too long")
                return

            folder_name = path.name
            if any(c in folder_name for c in
                   ['<', '>', ':', '"', '|', '?', '*']):
                messagebox.showerror("Error",
                                     "Invalid characters in folder name")
                return

            try:
                path = Path(path).resolve()
                if not path.exists() or not path.is_dir():
                    raise FileNotFoundError
                if not is_inside_root(path):
                    messagebox.showwarning("Blocked",
                                           "You can only browse files inside project_files.")
                    return
                current_dir = path
                path_var.set(str(current_dir))
                refresh()
            except Exception:
                messagebox.showerror("Invalid folder", f"Can't open:\n{path}")

        def go_up():
            if current_dir == root_dir:
                return
            go_to(current_dir.parent)

        def refresh():
            for item in folder_tree.get_children():
                folder_tree.delete(item)
            for item in file_tree.get_children():
                file_tree.delete(item)
            try:
                entries = sorted(current_dir.iterdir(),
                                 key=lambda p: (p.is_file(), p.name.lower()))
            except PermissionError:
                messagebox.showwarning("Permission denied",
                                       f"No access to:\n{current_dir}")
                return
            for p in entries:
                if p.is_dir():
                    folder_tree.insert("", "end", values=(p.name,))
                if p.is_file():
                    file_tree.insert("", "end", values=(
                        p.name,
                        p.stat().st_size if hasattr(p.stat(), 'st_size') else 0
                    ))

        def on_folder_open(event=None):
            sel = folder_tree.selection()
            if sel:
                folder_name = folder_tree.item(sel[0], "values")[0]
                go_to(current_dir / folder_name)

        def on_file_open(event=None):
            sel = file_tree.selection()
            if sel:
                file_name = file_tree.item(sel[0], "values")[0]
                full_path = (current_dir / file_name).resolve()
                if is_inside_root(full_path):
                    open_with_default_app(full_path)

        tk.Button(top, text="Up", command=go_up).pack(side="left", padx=(0, 6))
        tk.Button(top, text="Refresh", command=refresh).pack(side="left",
                                                             padx=(0, 6))
        body = tk.Frame(container, bg="#2e2e2e")
        body.pack(fill="both", expand=True, padx=10, pady=(0, 10))
        left_f = tk.LabelFrame(body, text="Folders", bg="#2e2e2e", fg="white")
        left_f.pack(side="left", fill="both", expand=True, padx=(0, 8))
        right_f = tk.LabelFrame(body, text="Files", bg="#2e2e2e", fg="white")
        right_f.pack(side="left", fill="both", expand=True)
        folder_tree = ttk.Treeview(left_f, columns=("name",), show="headings")
        folder_tree.heading("name", text="Name")
        folder_tree.pack(fill="both", expand=True)
        file_tree = ttk.Treeview(right_f, columns=("name", "size"),
                                 show="headings")
        file_tree.heading("name", text="Name")
        file_tree.heading("size", text="Size (bytes)")
        file_tree.pack(fill="both", expand=True)
        folder_tree.bind("<Double-1>", on_folder_open)
        file_tree.bind("<Double-1>", on_file_open)
        refresh()

    menu_items1 = ["File Manager", "Run Simulation", "Incident Reponse Tools",
                   "View Logs", "Recovery Options", "Profile", "Log Out",
                   "Back"]

    def on_menu_click(item_name: str) -> None:
        if item_name == "File Manager":
            show_in_right_panel(lambda parent: build_file_manager(parent))
            return
        if item_name == "Incident Reponse Tools":
            show_in_right_panel(
                lambda parent: IncidentResponsePage(parent, user_id, username,
                                                    db_manager).pack(
                    fill="both", expand=True))
            return
        if item_name == "View Logs":
            show_in_right_panel(lambda parent: LogsPageExternal(
                parent,
                on_back=lambda: build_placeholder(parent, "View logs"),
                db_manager=db_manager
            ))
            return
        if item_name == "Profile":
            show_in_right_panel(
                lambda parent: ProfilePage(parent, user_id, username,
                                           db_manager).pack(fill="both",
                                                            expand=True))
            return
        if item_name == "Log Out":
            if messagebox.askyesno("Logout",
                                   "Are you sure you want to log out?"):
                controller.show_login()
            return
        if item_name == "Run Simulation":
            show_in_right_panel(lambda parent: build_run_simulation(parent))
            return
        if item_name == "Recovery Options":
            project_root = Path(__file__).resolve().parent.parent
            project_files_root = (project_root / "project_files").resolve()
            show_in_right_panel(
                lambda parent: build_recovery_page(parent, project_files_root)
            )
            return

        show_in_right_panel(lambda parent: build_placeholder(parent, item_name))

    def make_menu_button(text: str) -> tk.Button:
        btn = tk.Button(
            left_panel1, text=text, font=("Arial", 14), bg="#d3d3d3",
            fg="black", anchor="w", padx=20, relief="flat", bd=0,
            highlightthickness=0, activebackground="#c0c0c0",
            command=lambda t=text: on_menu_click(t)
        )
        btn.pack(fill="x", pady=5)
        btn.configure(cursor="hand2")
        return btn

    for item in menu_items1:
        make_menu_button(item)

    build_placeholder(right_panel1, "Select a menu option...")
    return page_pre