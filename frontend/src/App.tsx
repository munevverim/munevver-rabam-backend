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

function getInitialTab() {
  const searchParams = new URLSearchParams(window.location.search);
  const tab = searchParams.get('tab');

  if (tab === 'services' || searchParams.has('carId')) {
    return 1;
  }

  if (tab === 'auditLogs') {
    return 2;
  }

  return 0;
}

function getTabName(tabIndex: number) {
  if (tabIndex === 1) {
    return 'services';
  }

  if (tabIndex === 2) {
    return 'auditLogs';
  }

  return 'cars';
}

function App() {
  const [activeTab, setActiveTab] = useState(getInitialTab);
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

  function handleTabChange(tabIndex: number) {
    setActiveTab(tabIndex);

    const searchParams = new URLSearchParams();
    searchParams.set('tab', getTabName(tabIndex));

    window.history.pushState(null, '', `?${searchParams.toString()}`);
  }

  function handleViewCarServices(carId: number) {
    const searchParams = new URLSearchParams();
    searchParams.set('tab', 'services');
    searchParams.set('carId', String(carId));

    window.history.pushState(null, '', `?${searchParams.toString()}`);
    setActiveTab(1);
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
            onChange={(_, newValue) => handleTabChange(newValue)}
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
          {activeTab === 0 && <CarsPage onViewServices={handleViewCarServices} />}
          {activeTab === 1 && <ServicesPage />}
          {activeTab === 2 && <AuditLogsPage />}
        </Box>
      </AppLayout>
    </ThemeProvider>
  );
}

export default App;
