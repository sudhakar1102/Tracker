export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: { sans: ['Figtree', 'system-ui', 'sans-serif'] },
      colors: {
        navy: { 900: '#0e1a3b', 800: '#16254f', 700: '#22336a' },
        violet: { 50: '#f4f1ff', 100: '#e9e3ff', 500: '#7c5cff', 600: '#6a49f0' },
      },
    },
  },
  plugins: [],
}
