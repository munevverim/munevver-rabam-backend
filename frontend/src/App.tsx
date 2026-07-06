import { Box, CssBaseline, Paper, Tab, Tabs, ThemeProvider } from '@mui/material';
import type { PaletteMode } from '@mui/material';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import BuildCircleIcon from '@mui/icons-material/BuildCircle';
import HistoryIcon from '@mui/icons-material/History';
import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import AppLayout from './components/layout/AppLayout';
import CarsPage from './pages/CarsPage';
import ServicesPage from './pages/ServicesPage';
import AuditLogsPage from './pages/AuditLogsPage';
import { getAppTheme } from './theme';

function App() {
  const [activeTab, setActiveTab] = useState(0);
  const { t } = useTranslation();

  const [mode, setMode] = useState<PaletteMode>(() => {
    const savedMode = localStorage.getItem('themeMode');

    if (savedMode === 'dark' || savedMode === 'light') {
      return savedMode;
    }

    return 'light';
  });

  const theme = useMemo(() => getAppTheme(mode), [mode]);

  function toggleThemeMode() {
    setMode((previousMode) => {
      const nextMode = previousMode === 'light' ? 'dark' : 'light';

      localStorage.setItem('themeMode', nextMode);

      return nextMode;
    });
  }

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />

      <AppLayout mode={mode} onToggleTheme={toggleThemeMode}>
        <Paper
          elevation={0}
          sx={(theme) => ({
            mb: 3,
            p: 1,
            borderRadius: 4,
            border: '1px solid',
            borderColor: 'divider',
            backgroundColor:
              theme.palette.mode === 'light'
                ? 'rgba(255,255,255,0.85)'
                : 'rgba(17,24,39,0.92)',
            backdropFilter: 'blur(10px)'
          })}
        >
          <Tabs
            value={activeTab}
            onChange={(_, newValue) => setActiveTab(newValue)}
            variant="fullWidth"
            sx={(theme) => ({
              '& .MuiTab-root': {
                minHeight: 54,
                fontWeight: 800,
                borderRadius: 3
              },
              '& .Mui-selected': {
                backgroundColor:
                  theme.palette.mode === 'light'
                    ? '#e8f3ff'
                    : 'rgba(25, 118, 210, 0.18)'
              },
              '& .MuiTabs-indicator': {
                display: 'none'
              }
            })}
          >
            <Tab
              icon={<DirectionsCarIcon />}
              iconPosition="start"
              label={t('tabs.cars')}
            />

            <Tab
              icon={<BuildCircleIcon />}
              iconPosition="start"
              label={t('tabs.services')}
            />

            <Tab
              icon={<HistoryIcon />}
              iconPosition="start"
              label={t('tabs.auditLogs')}
            />
          </Tabs>
        </Paper>

        <Box>
          {activeTab === 0 && <CarsPage />}
          {activeTab === 1 && <ServicesPage />}
          {activeTab === 2 && <AuditLogsPage />}
        </Box>
      </AppLayout>
    </ThemeProvider>
  );
}

export default App;