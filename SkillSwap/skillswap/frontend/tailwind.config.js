/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#1E3A5F',
          light: '#3D6FA5',
          50: '#EBF2FA',
          100: '#C3D9F0',
          200: '#9BC0E6',
          300: '#73A7DC',
          400: '#4B8ED2',
          500: '#3D6FA5',
          600: '#1E3A5F',
          700: '#162B47',
          800: '#0E1C2F',
          900: '#060D17'
        },
        secondary: '#3D6FA5',
        background: '#F4F6F8',
        success: '#16A34A',
        warning: '#D97706',
        error: '#DC2626'
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif']
      }
    }
  },
  plugins: []
}
