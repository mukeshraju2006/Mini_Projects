import tkinter as tk
from tkinter import messagebox
import requests

# --- Replace with your own OpenWeather API key ---
API_KEY = "8100a2fada41bd7273382a73bb623bf1"

# --- Function to fetch weather data ---
def get_weather():
    city = city_entry.get().strip()
    if not city:
        messagebox.showwarning("Input Error", "Please enter a city name.")
        return

    # 👇 Automatically assume Indian cities unless user specifies otherwise
    if "," not in city:
        city = f"{city},IN"

    url = f"https://api.openweathermap.org/data/2.5/weather?q={city}&appid={API_KEY}&units=metric"

    try:
        response = requests.get(url)
        data = response.json()

        if response.status_code == 200:
            city_name = data['name']
            country = data['sys']['country']
            temp = data['main']['temp']
            desc = data['weather'][0]['description']
            humidity = data['main']['humidity']
            wind = data['wind']['speed']

            result_label.config(
                text=f"📍 {city_name}, {country}\n"
                     f"🌡 Temperature: {temp}°C\n"
                     f"☁️ Weather: {desc.title()}\n"
                     f"💧 Humidity: {humidity}%\n"
                     f"🌬 Wind Speed: {wind} m/s",
                fg="white"
            )
        else:
            messagebox.showerror("Error", f"City not found: {data.get('message', 'unknown error')}")

    except Exception as e:
        messagebox.showerror("Error", f"Unable to fetch data.\n{e}")


# --- GUI Setup ---
root = tk.Tk()
root.title("🌦 Weather App")
root.geometry("400x400")
root.resizable(False, False)
root.configure(bg="#1e1e2e")

# Title
title_label = tk.Label(root, text="Weather App", font=("Arial", 20, "bold"), bg="#1e1e2e", fg="cyan")
title_label.pack(pady=15)

# City entry
city_entry = tk.Entry(root, width=30, font=("Arial", 14))
city_entry.pack(pady=10)
city_entry.insert(0, "Enter city name")

# Button
get_button = tk.Button(root, text="Get Weather", font=("Arial", 14), bg="cyan", fg="black", command=get_weather)
get_button.pack(pady=10)

# Result display
result_label = tk.Label(root, text="", font=("Arial", 12), justify="left", bg="#1e1e2e", fg="white")
result_label.pack(pady=20)

# Run app
root.mainloop()
