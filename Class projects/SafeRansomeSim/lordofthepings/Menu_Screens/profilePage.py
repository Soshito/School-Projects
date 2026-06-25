import tkinter as tk
from tkinter import ttk, messagebox
import random


class ProfilePage(tk.Frame):
    def __init__(self, parent, user_id, username, db_manager):
        super().__init__(parent)
        self.user_id = user_id
        self.username = username
        self.db = db_manager
        self.colors = {'bg': '#d9d9d9', 'dark': '#1e1e1e', 'teal': '#4fbdba'}
        self.configure(bg=self.colors['dark'])

        # --- NEW: Live Validation Registration ---
        # %P represents the value the text will have if the change is allowed
        self.v_alnum = (self.register(self.validate_alnum), '%P')
        self.v_num = (self.register(self.validate_numeric), '%P')

        self.entries = {}
        self.create_widgets()
        self.load_profile_data()

    # --- NEW: Live Validation Logic ---
    def validate_alnum(self, P):
        """Blocks symbols. Allows only letters, numbers, and spaces."""
        return all(c.isalnum() or c.isspace() or c in "@._-" for c in P) or P == ""

    def validate_numeric(self, P):
        """Blocks non-digits. Only numbers allowed in the box."""
        return P.isdigit() or P == ""

    def load_profile_data(self):
        """Loads data from the DB into the entries."""
        data = self.db.get_user_profile(self.user_id)
        if data:
            for key in ['email', 'school', 'age', 'gender']:
                if data.get(key) is not None:
                    # We temporarily disable validation to insert existing data
                    self.entries[key].config(validate="none")
                    self.entries[key].delete(0, tk.END)
                    self.entries[key].insert(0, str(data[key]))
                    # Re-enable validation after loading
                    self.entries[key].config(validate="key")

    def save_profile_data(self):
        """Final check and database update."""
        email = self.entries['email'].get().strip()
        school = self.entries['school'].get().strip()
        age = self.entries['age'].get().strip()
        gender = self.entries['gender'].get().strip()

        # Final safety check in case data was bypassed
        if age != "" and not age.isdigit():
            messagebox.showerror("Validation Error", "Age must be a number.")
            return

        success, msg = self.db.update_user_profile(self.user_id, email, school, age or None, gender)
        if success:
            messagebox.showinfo("Success", msg)
        else:
            messagebox.showerror("Error", msg)

    def generate_icon(self):
        """Teammate's original avatar generation logic."""
        self.canvas.delete("all")
        clrs = ["#ff6b6b", "#4fbdba", "#ffd93d", "#6c5ce7", "#74b9ff"]
        self.canvas.create_oval(10, 10, 140, 140, fill=random.choice(clrs), outline="")
        for _ in range(random.randint(3, 5)):
            c = random.choice(clrs)
            x, y = random.randint(20, 80), random.randint(20, 80)
            if random.random() > 0.5:
                self.canvas.create_oval(x, y, x + 40, y + 40, fill=c, outline="")
            else:
                self.canvas.create_rectangle(x, y, x + 40, y + 40, fill=c, outline="")

    def create_widgets(self):
        main = tk.Frame(self, bg=self.colors['bg'])
        main.pack(fill=tk.BOTH, expand=True, padx=20, pady=20)

        hdr = tk.Frame(main, bg=self.colors['teal'], height=45)
        hdr.pack(fill=tk.X)
        tk.Label(hdr, text="Profile Settings", bg=self.colors['teal'], font=('Arial', 14, 'bold')).pack(expand=True)

        content = tk.Frame(main, bg=self.colors['bg'])
        content.pack(fill=tk.BOTH, expand=True, padx=40, pady=20)

        # Left Column: Avatar Generator
        left_col = tk.Frame(content, bg=self.colors['bg'])
        left_col.pack(side=tk.LEFT, anchor='n', padx=(0, 40))

        self.canvas = tk.Canvas(left_col, width=150, height=150, bg=self.colors['bg'], highlightthickness=0)
        self.canvas.pack()
        self.generate_icon()
        tk.Button(left_col, text="New Icon", command=self.generate_icon, bg="#ffffff").pack(pady=5)

        # Right Column: Data Entry
        right_col = tk.Frame(content, bg=self.colors['bg'])
        right_col.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)

        tk.Label(right_col, text="Username:", bg=self.colors['bg'], font=('Arial', 10, 'bold')).pack(anchor='w',
                                                                                                     pady=(5, 0))
        tk.Label(right_col, text=self.username, bg=self.colors['bg'], font=('Arial', 11, 'italic')).pack(anchor='w')

        # Updated Field Configs with Validation
        fields = [
            ("Email", "email", self.v_alnum),
            ("University / School", "school", self.v_alnum),
            ("Age", "age", self.v_num),
            ("Gender", "gender", self.v_alnum)
        ]

        for label_text, key, v_cmd in fields:
            tk.Label(right_col, text=f"{label_text}:", bg=self.colors['bg'], font=('Arial', 10, 'bold')).pack(
                anchor='w', pady=(10, 0))
            # Added validate="key" and validatecommand to the entries
            ent = tk.Entry(right_col, bg="white", width=35, validate="key", validatecommand=v_cmd)
            ent.pack(anchor='w', pady=2)
            self.entries[key] = ent

        btn_frame = tk.Frame(right_col, bg=self.colors['bg'])
        btn_frame.pack(anchor='w', pady=30)

        tk.Button(btn_frame, text="Save Changes", bg=self.colors['teal'], font=('Arial', 10, 'bold'),
                  command=self.save_profile_data, width=15).pack(side=tk.LEFT, padx=(0, 10))

