/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "#E50914", // Netflix Red
        background: "#141414",
        surface: "#181818",
      }
    },
  },
  plugins: [],
}
