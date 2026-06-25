import tkinter as tk
from tkinter import ttk, messagebox


class IncidentResponsePage(tk.Frame):
    def __init__(self, parent, user_id, username, db_manager):
        super().__init__(parent)
        self.user_id = user_id
        self.username = username
        self.db = db_manager
        self.colors = {'bg': '#d9d9d9', 'dark': '#1e1e1e', 'teal': '#4fbdba'}
        self.configure(bg=self.colors['dark'])

        # Educational database of real-world ransomware extensions
        self.strains = {
            ".wannacry": {
                "name": "WannaCry",
                "year": "2017",
                "type": "Wormable Ransomware",
                "impact": "Infected 230,000+ computers in 150 countries. Famous for using the EternalBlue exploit leaked from the NSA."
            },
            ".petya": {
                "name": "Petya / NotPetya",
                "year": "2016",
                "type": "Boot-level Encryptor",
                "impact": "Targets the Master Boot Record (MBR) to prevent the OS from booting entirely. NotPetya was later used in global supply chain attacks."
            },
            ".locky": {
                "name": "Locky",
                "year": "2016",
                "type": "Macro-based Trojan",
                "impact": "One of the most successful early strains, distributed primarily via malicious Word document macros in phishing emails."
            },
            ".ryuk": {
                "name": "Ryuk",
                "year": "2018",
                "type": "Targeted Enterprise Threat",
                "impact": "Known for 'Big Game Hunting,' targeting high-revenue organizations for massive payouts rather than individual users."
            },
            ".darkside": {
                "name": "DarkSide",
                "year": "2021",
                "type": "RaaS (Ransomware as a Service)",
                "impact": "Responsible for the Colonial Pipeline attack which caused fuel shortages across the US East Coast."
            },
            ".lockbit": {
                "name": "LockBit",
                "year": "2019",
                "type": "Data Exfiltration Threat",
                "impact": "Popularized 'Double Extortion,' where they don't just lock files, but also steal data and threaten to leak it publicly."
            },
            ".revil": {
                "name": "REvil (Sodinokibi)",
                "year": "2019",
                "type": "Highly Advanced RaaS",
                "impact": "Extremely sophisticated code. Responsible for the massive Kaseya attack that impacted thousands of businesses."
            },
            ".hive": {
                "name": "Hive",
                "year": "2021",
                "type": "Multi-platform Ransomware",
                "impact": "Known for aggressively targeting healthcare providers and public infrastructure with unique encryption methods."
            }
        }

        self.create_widgets()

    def create_widgets(self):
        main = tk.Frame(self, bg=self.colors['bg'])
        main.pack(fill=tk.BOTH, expand=True, padx=20, pady=20)

        hdr = tk.Frame(main, bg=self.colors['teal'], height=45)
        hdr.pack(fill=tk.X)
        tk.Label(hdr, text="Threat Intelligence & Forensics", bg=self.colors['teal'],
                 font=('Arial', 14, 'bold')).pack(expand=True)

        content = tk.Frame(main, bg=self.colors['bg'])
        content.pack(fill=tk.BOTH, expand=True, padx=20, pady=20)

        # Tool Container
        strain_frame = tk.LabelFrame(content, text=" Ransomware Signature Database ", bg=self.colors['bg'],
                                     font=('Arial', 10, 'bold'), padx=20, pady=20)
        strain_frame.pack(fill=tk.BOTH, expand=True, pady=10)

        tk.Label(strain_frame, text="Enter a suspected malware file extension:",
                 bg=self.colors['bg'], font=('Arial', 11)).pack(anchor='w', pady=(0, 10))

        # Input Area
        input_frame = tk.Frame(strain_frame, bg=self.colors['bg'])
        input_frame.pack(fill=tk.X)

        self.ext_entry = tk.Entry(input_frame, width=15, font=('Arial', 12))
        self.ext_entry.pack(side=tk.LEFT, padx=(0, 10))

        tk.Button(input_frame, text="Search Intelligence", command=self.analyze_strain,
                  bg=self.colors['teal'], font=('Arial', 9, 'bold'), width=18).pack(side=tk.LEFT, padx=5)

        # NEW: Help/Directory Button
        tk.Button(input_frame, text="?", command=self.show_directory,
                  bg="#ffffff", font=('Arial', 9, 'bold'), width=3).pack(side=tk.LEFT, padx=5)

        # Detailed Results Area
        self.report_frame = tk.Frame(strain_frame, bg=self.colors['bg'], pady=20)
        self.report_frame.pack(fill=tk.BOTH, expand=True)

        self.res_title = tk.Label(self.report_frame, text="System Idle: Awaiting Analysis",
                                  bg=self.colors['bg'], font=('Arial', 11, 'bold'))
        self.res_title.pack(anchor='w')

        self.res_body = tk.Label(self.report_frame, text="", bg=self.colors['bg'],
                                 font=('Arial', 10), justify=tk.LEFT, wraplength=550)
        self.res_body.pack(anchor='w', pady=10)

    def show_directory(self):
        """Displays a pop-up with a list of searchable extensions."""
        dir_win = tk.Toplevel(self)
        dir_win.title("Intelligence Directory")
        dir_win.geometry("300x400")
        dir_win.configure(bg=self.colors['bg'])

        tk.Label(dir_win, text="Searchable Extensions", bg=self.colors['bg'],
                 font=('Arial', 11, 'bold')).pack(pady=15)

        # Sort keys for a nice alphabetical list
        ext_list = sorted(self.strains.keys())

        for ext in ext_list:
            name = self.strains[ext]['name']
            tk.Label(dir_win, text=f"{ext} ({name})", bg=self.colors['bg'],
                     font=('Arial', 10)).pack(pady=2, anchor='w', padx=50)

        tk.Button(dir_win, text="Close", command=dir_win.destroy, bg=self.colors['teal']).pack(pady=20)

    def analyze_strain(self):
        ext = self.ext_entry.get().lower().strip()
        if ext and not ext.startswith("."):
            ext = "." + ext

        if ext in self.strains:
            info = self.strains[ext]
            self.res_title.config(text=f"THREAT IDENTIFIED: {info['name']}", fg="#d9534f")
            report = (f"Classification: {info['type']}\n"
                      f"Active Since: ~{info['year']}\n\n"
                      f"Historical Significance:\n{info['impact']}")
            self.res_body.config(text=report, fg="black")
        else:
            self.res_title.config(text="NO SIGNATURE MATCH", fg="#333333")
            self.res_body.config(text="This extension is not currently in the database.\n"
                                      "Click the '?' button for a list of valid signatures.")