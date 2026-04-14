/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: '#141414', // Netflix black
        primary: '#E50914',    // Netflix red
        surface: '#181818',
      }
    },
  },
  plugins: [],
}
