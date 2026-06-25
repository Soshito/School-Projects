import tkinter as tk
from tkinter import messagebox
import mysql.connector
from mysql.connector import Error
import bcrypt
from datetime import datetime
import re


class DatabaseManager:
    def __init__(self, host, database, user, password):
        self.config = {
            'host': host,
            'database': database,
            'user': user,
            'password': password
        }
        self.connection = None
        self.offline_mode = False

    def connect(self):
        try:
            self.connection = mysql.connector.connect(**self.config)
            print(f"DEBUG: Connected to {self.config['host']}")
            self.offline_mode = False
            return True
        except Error as e:
            print(f"DEBUG: Connection failed: {e}")
            self.offline_mode = True
            return False

    def get_user_profile(self, user_id):
        if self.offline_mode:
            return None
        try:
            if not self.connection or not self.connection.is_connected(): self.connect()
            cursor = self.connection.cursor(dictionary=True)
            query = "SELECT email, school, age, gender FROM users WHERE idusers = %s"
            cursor.execute(query, (user_id,))
            result = cursor.fetchone()
            cursor.close()
            return result
        except Error as e:
            return None

    def update_user_profile(self, user_id, email, school, age, gender):
        if self.offline_mode:
            return False, "Cannot save in offline mode"
        try:
            if not self.connection or not self.connection.is_connected(): self.connect()
            cursor = self.connection.cursor()
            query = """UPDATE users 
                       SET email = %s, school = %s, age = %s, gender = %s 
                       WHERE idusers = %s"""
            cursor.execute(query, (email, school, age, gender, user_id))
            self.connection.commit()
            cursor.close()
            return True, "Profile updated successfully"
        except Error as e:
            return False, f"Update failed: {e}"

    def verify_user(self, username, password):
        if not self.connection:
            self.connect()
        if self.connection:
            return self._verify_database_user(username, password)
        return self._verify_offline_user(username, password)

    def _verify_database_user(self, username, password):
        try:
            cursor = self.connection.cursor(dictionary=True)
            query = "SELECT idusers, username, password_hash FROM users WHERE username = %s"
            cursor.execute(query, (username,))
            user = cursor.fetchone()
            cursor.close()

            if user and bcrypt.checkpw(password.encode('utf-8'),
                                       user['password_hash'].encode('utf-8')):
                return user['idusers'], user['username']
            return None
        except Error as e:
            return None

    def _verify_offline_user(self, username, password):
        offline_account = {
            'administrator': {'password': 'administrator', 'user_id': 999}}
        account = offline_account.get(username)
        if account and account['password'] == password:
            return -account['user_id'], f"{username} (OFFLINE)"
        return None

    def validate_registration_input(self, username, password, email=None):
        """Validate username and password before registration"""
        errors = []

        # Username validation
        if not username or len(username.strip()) < 3:
            errors.append("Username must be at least 3 characters")
        elif not re.match(r'^[a-zA-Z0-9_]{3,20}$', username):
            errors.append(
                "Username: letters, numbers, underscores only (3-20 chars)")

        # Password validation
        if not password or len(password) < 10:
            errors.append("Password must be at least 10 characters")
        elif len(password) > 128:
            errors.append("Password too long (max 128 characters)")

        # Optional email validation
        if email:
            if not re.match(r'^[\w\.-]+@[\w\.-]+\.\w+$', email):
                errors.append("Invalid email format")

        return len(errors) == 0, errors

    def register_user(self, username, password, email=None):
        # VALIDATE INPUT FIRST
        is_valid, errors = self.validate_registration_input(username, password,
                                                            email)
        if not is_valid:
            return False, "\n".join(errors)

        if not self.connection:
            if not self.connect():
                return False, "Cannot register offline - no database connection"
        try:
            cursor = self.connection.cursor()
            cursor.execute("SELECT idusers FROM users WHERE username = %s",
                           (username,))
            if cursor.fetchone():
                cursor.close()
                return False, "Username already exists"

            hashed = bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt())
            query = "INSERT INTO users (username, password_hash, created_at) VALUES (%s, %s, %s)"
            cursor.execute(query,
                           (username, hashed.decode('utf-8'), datetime.now()))
            self.connection.commit()
            cursor.close()
            return True, "User registered successfully"
        except Error as e:
            return False, f"Registration failed: {e}"

    def update_password(self, user_id, new_password):
        if self.offline_mode:
            return False, "Cannot change password in offline mode"
        try:
            cursor = self.connection.cursor()
            hashed = bcrypt.hashpw(new_password.encode('utf-8'),
                                   bcrypt.gensalt())
            query = "UPDATE users SET password_hash = %s WHERE idusers = %s"
            cursor.execute(query, (hashed.decode('utf-8'), user_id))
            self.connection.commit()
            cursor.close()
            return True, "Password updated successfully"
        except Error as e:
            return False, f"Database error: {e}"

    def is_offline(self):
        return self.offline_mode

    def get_simulation_logs(self, limit=100, user_id=None):
        """Retrieve simulation logs from database"""
        if not self.connection:
            if not self.connect():
                return []

        try:
            cursor = self.connection.cursor(dictionary=True)
            if user_id:
                query = "SELECT * FROM simulation_logs WHERE user_id = %s ORDER BY timestamp DESC LIMIT %s"
                cursor.execute(query, (user_id, limit))
            else:
                query = "SELECT * FROM simulation_logs ORDER BY timestamp DESC LIMIT %s"
                cursor.execute(query, (limit,))

            results = cursor.fetchall()
            cursor.close()
            return results
        except Error as e:
            print(f"Error fetching logs: {e}")
            return []

    def close(self):
        if self.connection and self.connection.is_connected():
            self.connection.close()


class ConsentScreen(tk.Frame):
    def __init__(self, parent, on_consent_given, colors=None):
        super().__init__(parent)
        self.on_consent_given = on_consent_given

        self.colors = colors or {
            'bg_dark': '#1e1e1e', 'bg_medium': '#2d3e3e',
            'header_teal': '#4fbdba', 'text_light': '#ffffff',
            'text_dark': '#1e1e1e', 'warning_red': '#ff4444',
            'success_green': '#7fe9a2'
        }
        self.configure(bg=self.colors['bg_dark'])
        self.create_widgets()

    def create_widgets(self):
        main_frame = tk.Frame(self, bg=self.colors['bg_medium'], padx=40,
                              pady=30)
        main_frame.place(relx=0.5, rely=0.5, anchor=tk.CENTER, width=600,
                         height=500)

        header = tk.Frame(main_frame, bg=self.colors['warning_red'], height=50)
        header.pack(fill=tk.X, pady=(0, 20))
        header.pack_propagate(False)
        tk.Label(header, text="⚠️ EDUCATIONAL USE ONLY",
                 bg=self.colors['warning_red'], fg='white',
                 font=('Arial', 16, 'bold')).pack(expand=True)

        warning_text = """This application contains a ransomware simulation for educational purposes only.

Safety Features:
• Simulations are confined to the 'project_files/' directory only
• Personal files and system directories are NEVER accessed
• Recovery options are provided to restore simulated files
• All activities are logged for educational review

By proceeding, you acknowledge this tool is for learning cybersecurity defense mechanisms only."""

        tk.Label(main_frame, text=warning_text, bg=self.colors['bg_medium'],
                 fg=self.colors['text_light'], font=('Arial', 11),
                 justify=tk.LEFT, wraplength=520).pack(pady=10)

        self.consent_var = tk.BooleanVar(value=False)
        tk.Checkbutton(main_frame,
                       text="I understand and agree to use this application for educational purposes only",
                       variable=self.consent_var, bg=self.colors['bg_medium'],
                       fg=self.colors['text_light'],
                       selectcolor=self.colors['bg_dark'],
                       activebackground=self.colors['bg_medium'],
                       activeforeground=self.colors['text_light'],
                       font=('Arial', 10, 'bold'),
                       command=self.toggle_button).pack(pady=20)

        btn_frame = tk.Frame(main_frame, bg=self.colors['bg_medium'])
        btn_frame.pack(fill=tk.X, pady=10)

        self.accept_btn = tk.Button(btn_frame, text="Accept & Continue",
                                    bg=self.colors['success_green'],
                                    fg=self.colors['text_dark'],
                                    font=('Arial', 12, 'bold'),
                                    state=tk.DISABLED,
                                    command=self.accept, width=15)
        self.accept_btn.pack(side=tk.LEFT, padx=(50, 10))

        tk.Button(btn_frame, text="Decline & Exit",
                  bg=self.colors['warning_red'], fg='white',
                  font=('Arial', 12, 'bold'), command=self.decline,
                  width=15).pack(side=tk.RIGHT, padx=(10, 50))

    def toggle_button(self):
        self.accept_btn.config(
            state=tk.NORMAL if self.consent_var.get() else tk.DISABLED)

    def accept(self):
        self.on_consent_given()

    def decline(self):
        if messagebox.askyesno("Exit", "Are you sure you want to exit?"):
            self.quit()
            self.destroy()


class LoginPage(tk.Frame):
    def __init__(self, parent, on_login_success, db_manager=None):
        super().__init__(parent)
        self.on_login_success = on_login_success
        self.db = db_manager or DatabaseManager()
        self.colors = {
            'bg_dark': '#1e1e1e', 'bg_medium': '#2d3e3e',
            'header_teal': '#4fbdba',
            'entry_cyan': '#b2ffff', 'text_light': '#ffffff',
            'text_dark': '#1e1e2e',
            'error_red': '#ff6b6b', 'warning_yellow': '#ffd93d',
            'warning_red': '#ff4444', 'success_green': '#7fe9a2'
        }
        self.configure(bg=self.colors['bg_dark'])
        self.create_widgets()
        self.check_connection()

    def check_connection(self):
        if self.db.is_offline():
            warning = tk.Label(self,
                               text="⚠ OFFLINE MODE - Using local accounts",
                               bg=self.colors['bg_dark'],
                               fg=self.colors['warning_yellow'],
                               font=('Arial', 10, 'bold'))
            warning.place(relx=0.5, rely=0.05, anchor=tk.CENTER)

    def create_widgets(self):
        main_frame = tk.Frame(self, bg=self.colors['bg_medium'])
        main_frame.place(relx=0.5, rely=0.5, anchor=tk.CENTER, width=550,
                         height=450)
        header_frame = tk.Frame(main_frame, bg=self.colors['header_teal'],
                                height=45)
        header_frame.pack(fill=tk.X)
        tk.Label(header_frame, text="Login", bg=self.colors['header_teal'],
                 fg=self.colors['text_dark'],
                 font=('Arial', 14, 'bold', 'italic', 'underline')).pack(
            expand=True)
        content_frame = tk.Frame(main_frame, bg=self.colors['bg_medium'])
        content_frame.pack(pady=20)
        self.error_label = tk.Label(content_frame, text="",
                                    bg=self.colors['bg_medium'],
                                    fg=self.colors['error_red'])
        self.error_label.pack(pady=(0, 10))
        tk.Label(content_frame, text="Username", bg=self.colors['bg_medium'],
                 fg=self.colors['text_dark'],
                 font=('Arial', 11, 'bold')).pack(anchor='w')
        self.user_entry = tk.Entry(content_frame, bg=self.colors['entry_cyan'],
                                   bd=0, width=30, font=('Arial', 12))
        self.user_entry.pack(pady=(5, 15), ipady=8)
        tk.Label(content_frame, text="Password", bg=self.colors['bg_medium'],
                 fg=self.colors['text_dark'],
                 font=('Arial', 11, 'bold')).pack(anchor='w')
        self.pass_entry = tk.Entry(content_frame, bg=self.colors['entry_cyan'],
                                   bd=0, width=30, show="*", font=('Arial', 12))
        self.pass_entry.pack(pady=5, ipady=8)
        self.pass_entry.bind('<Return>', lambda e: self.handle_login())
        btn_frame = tk.Frame(main_frame, bg=self.colors['bg_medium'])
        btn_frame.pack(fill=tk.X, side=tk.BOTTOM, padx=40, pady=20)
        tk.Button(btn_frame, text="Login", bg=self.colors['entry_cyan'],
                  command=self.handle_login).pack(side=tk.LEFT)
        if not self.db.is_offline():
            tk.Button(btn_frame, text="Register", bg=self.colors['bg_medium'],
                      fg=self.colors['entry_cyan'],
                      command=self.show_register_dialog).pack(side=tk.RIGHT)

    def handle_login(self):
        username = self.user_entry.get().strip()
        password = self.pass_entry.get()

        # Basic validation
        if not username or not password:
            self.error_label.config(
                text="Please enter both username and password")
            return

        # Length check
        if len(username) > 20:
            self.error_label.config(text="Username too long")
            return

        result = self.db.verify_user(username, password)
        if result:
            # Show consent screen instead of going directly to main app
            self.show_consent_screen(result[0], result[1])
        else:
            self.error_label.config(text="Invalid credentials")

    def show_consent_screen(self, user_id, username):
        """Show educational consent warning after successful login"""
        # Clear login widgets
        for widget in self.winfo_children():
            widget.destroy()

        # Show consent screen
        consent = ConsentScreen(self,
                                on_consent_given=lambda: self.on_login_success(
                                    user_id, username),
                                colors=self.colors)
        consent.pack(fill="both", expand=True)

    def show_register_dialog(self):
        dialog = tk.Toplevel(self)
        dialog.title("Register")
        dialog.geometry("350x300")
        dialog.configure(bg=self.colors['bg_medium'])

        # Error display label
        error_label = tk.Label(dialog, text="", bg=self.colors['bg_medium'],
                               fg=self.colors['error_red'], wraplength=300,
                               justify='center')
        error_label.pack(pady=(10, 0))

        tk.Label(dialog, text="Username", bg=self.colors['bg_medium'],
                 font=('Arial', 10, 'bold')).pack(pady=5)
        new_u = tk.Entry(dialog, width=30, font=('Arial', 11),
                         bg=self.colors['entry_cyan'])
        new_u.pack(pady=5)

        tk.Label(dialog, text="Password", bg=self.colors['bg_medium'],
                 font=('Arial', 10, 'bold')).pack(pady=5)
        new_p = tk.Entry(dialog, show="*", width=30, font=('Arial', 11),
                         bg=self.colors['entry_cyan'])
        new_p.pack(pady=5)

        def reg():
            username = new_u.get().strip()
            password = new_p.get()

            # Call validation method - THIS NOW WORKS
            is_valid, errors = self.db.validate_registration_input(username,
                                                                   password)
            if not is_valid:
                error_label.config(text="\n".join(errors))
                return

            success, msg = self.db.register_user(username, password)
            if success:
                messagebox.showinfo("Success", msg)
                dialog.destroy()
            else:
                error_label.config(text=msg)

        tk.Button(dialog, text="Register", bg=self.colors['entry_cyan'],
                  font=('Arial', 11, 'bold'), command=reg).pack(pady=15)