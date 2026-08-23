/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#f0f7ff',
          100: '#e0effe',
          200: '#b9dffe',
          300: '#7cc5fd',
          400: '#36a8fa',
          500: '#0c8ce9',
          600: '#026fc7',
          700: '#0358a1',
          800: '#074b85',
          900: '#0c3f6e',
          950: '#082849',
        },
        razor: {
          blue: '#0C2340',
          navy: '#020B18',
          accent: '#3395FF',
          green: '#10B981',
          gold: '#F59E0B'
        }
      }
    },
  },
  plugins: [],
}