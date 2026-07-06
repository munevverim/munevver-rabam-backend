import {
  AppBar,
  Box,
  Container,
  FormControl,
  IconButton,
  MenuItem,
  Select,
  Toolbar,
  Tooltip,
  Typography
} from '@mui/material';
import type { PaletteMode, SelectChangeEvent } from '@mui/material';
import DarkModeIcon from '@mui/icons-material/DarkMode';
import LightModeIcon from '@mui/icons-material/LightMode';
import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import logo from '../../assets/rabam_logo.jpg';

type AppLayoutProps = {
  children: ReactNode;
  mode: PaletteMode;
  onToggleTheme: () => void;
};

function AppLayout({ children, mode, onToggleTheme }: AppLayoutProps) {
  const { t, i18n } = useTranslation();

  function handleLanguageChange(event: SelectChangeEvent) {
    const language = event.target.value;

    i18n.changeLanguage(language);
    localStorage.setItem('language', language);
  }

  return (
    <Box
      sx={(theme) => ({
        minHeight: '100vh',
        background:
          theme.palette.mode === 'light'
            ? 'linear-gradient(180deg, #eef6ff 0%, #f7f9fc 40%, #f3f6fb 100%)'
            : 'linear-gradient(180deg, #0f172a 0%, #111827 45%, #020617 100%)'
      })}
    >
      <AppBar
        position="static"
        elevation={0}
        sx={{
          background:
            'linear-gradient(135deg, #1976d2 0%, #0b74de 45%, #00a6ff 100%)',
          borderBottomLeftRadius: 24,
          borderBottomRightRadius: 24,
          boxShadow: '0 12px 40px rgba(25, 118, 210, 0.25)'
        }}
      >
        <Container maxWidth="lg">
          <Toolbar
            disableGutters
            sx={{
              minHeight: 92,
              gap: 2.5
            }}
          >
            <Box
              component="img"
              src={logo}
              alt="Rabam Logo"
              sx={{
                width: 64,
                height: 64,
                objectFit: 'cover',
                borderRadius: 3,
                backgroundColor: 'white',
                p: 0.7,
                boxShadow: '0 8px 22px rgba(0, 0, 0, 0.18)'
              }}
            />

            <Box sx={{ flex: 1 }}>
              <Typography variant="h4" sx={{ lineHeight: 1.1 }}>
                {t('app.title')}
              </Typography>
              <Typography
                variant="body1"
                sx={{
                  mt: 0.8,
                  color: 'rgba(255,255,255,0.9)'
                }}
              >
                {t('app.subtitle')}
              </Typography>
            </Box>

            <FormControl
              size="small"
              sx={{
                minWidth: 92,
                '& .MuiOutlinedInput-root': {
                  color: 'white',
                  backgroundColor: 'rgba(255,255,255,0.16)',
                  borderRadius: 3
                },
                '& .MuiOutlinedInput-notchedOutline': {
                  borderColor: 'rgba(255,255,255,0.35)'
                },
                '& .MuiSvgIcon-root': {
                  color: 'white'
                }
              }}
            >
              <Select
                value={i18n.language}
                onChange={handleLanguageChange}
              >
                <MenuItem value="tr">TR</MenuItem>
                <MenuItem value="en">EN</MenuItem>
              </Select>
            </FormControl>

            <Tooltip title={mode === 'light' ? t('layout.switchToDark') : t('layout.switchToLight')}>
              <IconButton
                onClick={onToggleTheme}
                sx={{
                  color: 'white',
                  backgroundColor: 'rgba(255,255,255,0.16)',
                  '&:hover': {
                    backgroundColor: 'rgba(255,255,255,0.26)'
                  }
                }}
              >
                {mode === 'light' ? <DarkModeIcon /> : <LightModeIcon />}
              </IconButton>
            </Tooltip>
          </Toolbar>
        </Container>
      </AppBar>

      <Container maxWidth="lg" sx={{ py: 4 }}>
        {children}
      </Container>
    </Box>
  );
}

export default AppLayout;