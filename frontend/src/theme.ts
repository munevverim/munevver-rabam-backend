import { createTheme } from '@mui/material/styles';
import type { PaletteMode } from '@mui/material';

export function getAppTheme(mode: PaletteMode) {
  return createTheme({
    palette: {
      mode,
      primary: {
        main: '#1976d2'
      },
      secondary: {
        main: '#00a6ff'
      },
      background: {
        default: mode === 'light' ? '#f3f6fb' : '#0f172a',
        paper: mode === 'light' ? '#ffffff' : '#111827'
      }
    },
    shape: {
      borderRadius: 14
    },
    typography: {
      fontFamily: [
        'Inter',
        'Segoe UI',
        'Roboto',
        'Arial',
        'sans-serif'
      ].join(','),
      h4: {
        fontWeight: 800
      },
      h5: {
        fontWeight: 700
      },
      h6: {
        fontWeight: 700
      }
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            textTransform: 'none',
            fontWeight: 700,
            borderRadius: 10
          }
        }
      },
      MuiCard: {
        styleOverrides: {
          root: {
            borderRadius: 18,
            boxShadow:
              mode === 'light'
                ? '0 10px 30px rgba(15, 23, 42, 0.08)'
                : '0 10px 30px rgba(0, 0, 0, 0.35)'
          }
        }
      },
      MuiTextField: {
        defaultProps: {
          size: 'small'
        }
      },
      MuiPaper: {
        styleOverrides: {
          root: {
            backgroundImage: 'none'
          }
        }
      }
    }
  });
}