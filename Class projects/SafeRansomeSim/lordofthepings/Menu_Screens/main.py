import tkinter as tk
from tkinter import ttk
from menu_screen import create_pre_page
from loginPage import LoginPage, DatabaseManager


class MainApp:
    def __init__(self, root):
        self.root = root
        self.root.title("Menu_Screens")
        self.root.geometry("1000x850")
        self.container = tk.Frame(self.root)
        self.container.pack(expand=True, fill="both")
        self.db_manager = DatabaseManager(
            host='100.96.160.70', database='saferansomsimdb',
            user='LOTP_user', password='S3F3dei43dk4ooo$$'
        )
        self.current_user_id = None
        self.current_username = None
        self.show_login()

    def show_login(self):
        for widget in self.container.winfo_children():
            widget.destroy()
        self.login_page = LoginPage(self.container, on_login_success=self.show_main_menu, db_manager=self.db_manager)
        self.login_page.pack(expand=True, fill="both")

    def show_main_menu(self, user_id, username):
        for widget in self.container.winfo_children():
            widget.destroy()
        self.current_user_id = user_id
        self.current_username = username
        self.notebook = ttk.Notebook(self.container)
        self.notebook.pack(expand=True, fill="both")

        # Updated to pass user session data and db_manager
        page_pre = create_pre_page(self.notebook, self, self.current_user_id, self.current_username, self.db_manager)
        self.notebook.add(page_pre, text="Main Screen")


def main():
    root = tk.Tk()
    app = MainApp(root)
    root.mainloop()


if __name__ == "__main__":
    main()