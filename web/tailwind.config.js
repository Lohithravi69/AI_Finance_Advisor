/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: [
    './index.html',
    './src/**/*.{ts,tsx}'
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#f2fbff',
          100: '#e4f7ff',
          200: '#bfeeff',
          300: '#8fe2ff',
          400: '#57d3ff',
          500: '#2ac0ff',
          600: '#109bdc',
          700: '#0e7bb0',
          800: '#0f628b',
          900: '#114f72'
        }
      }
    }
  },
  plugins: []
}
