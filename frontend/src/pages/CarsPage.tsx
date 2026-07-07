import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Divider,
  IconButton,
  InputAdornment,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  TextField,
  Typography
} from '@mui/material';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import BuildCircleIcon from '@mui/icons-material/BuildCircle';
import EditIcon from '@mui/icons-material/Edit';
import RefreshIcon from '@mui/icons-material/Refresh';
import SaveIcon from '@mui/icons-material/Save';
import BadgeIcon from '@mui/icons-material/Badge';
import FactoryIcon from '@mui/icons-material/Factory';
import TimeToLeaveIcon from '@mui/icons-material/TimeToLeave';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import type { CarRequest, CarResponse } from '../types/car';
import type { DashboardSummaryResponse } from '../types/dashboard';
import { createCar, getCars, updateCar } from '../api/carApi';
import { getDashboardSummary } from '../api/dashboardApi';

type CarsPageProps = {
  onViewServices: (carId: number) => void;
};

function CarsPage({ onViewServices }: CarsPageProps) {
  const { t } = useTranslation();

  const [cars, setCars] = useState<CarResponse[]>([]);
  const [summary, setSummary] = useState<DashboardSummaryResponse | null>(null);

  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  const [licensePlate, setLicensePlate] = useState('');
  const [brand, setBrand] = useState('');
  const [model, setModel] = useState('');

  const [editingCarId, setEditingCarId] = useState<number | null>(null);

  const [loading, setLoading] = useState(false);
  const [summaryLoading, setSummaryLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  async function loadCars() {
    try {
      setLoading(true);
      setErrorMessage('');

      const result = await getCars(page, size);

      setCars(result.content);
      setTotalElements(result.totalElements);
    } catch (error) {
      setErrorMessage(getErrorMessage(error));
    } finally {
      setLoading(false);
    }
  }

  async function loadSummary() {
    try {
      setSummaryLoading(true);

      const result = await getDashboardSummary();

      setSummary(result);
    } catch (error) {
      setErrorMessage(getErrorMessage(error));
    } finally {
      setSummaryLoading(false);
    }
  }

  async function refreshPageData() {
    await loadCars();
    await loadSummary();
  }

  useEffect(() => {
    loadCars();
  }, [page, size]);

  useEffect(() => {
    loadSummary();
  }, []);

  async function handleSubmit() {
    try {
      setSaving(true);
      setErrorMessage('');
      setSuccessMessage('');

      const request: CarRequest = {
        licensePlate,
        brand,
        model
      };

      if (editingCarId) {
        await updateCar(editingCarId, request);
        setSuccessMessage(t('cars.updated'));
      } else {
        await createCar(request);
        setSuccessMessage(t('cars.created'));
        setPage(0);
      }

      resetForm();

      await loadCars();
      await loadSummary();
    } catch (error) {
      setErrorMessage(getErrorMessage(error));
    } finally {
      setSaving(false);
    }
  }

  function handleEdit(car: CarResponse) {
    setEditingCarId(car.id);
    setLicensePlate(car.licensePlate);
    setBrand(car.brand);
    setModel(car.model);
    setErrorMessage('');
    setSuccessMessage('');
  }

  function resetForm() {
    setEditingCarId(null);
    setLicensePlate('');
    setBrand('');
    setModel('');
  }

  function isFormValid() {
    return licensePlate.trim() !== '' && brand.trim() !== '' && model.trim() !== '';
  }

  function formatDate(value: string | null) {
    if (!value) {
      return '-';
    }

    return new Date(value).toLocaleString('tr-TR');
  }

  function getErrorMessage(error: unknown) {
    if (axios.isAxiosError(error)) {
      const responseData = error.response?.data;

      if (responseData?.message) {
        return responseData.message;
      }

      if (responseData?.errors) {
        return Object.values(responseData.errors).join(', ');
      }

      if (error.message === 'Network Error') {
        return t('common.backendNotReachable');
      }

      if (error.message) {
        return error.message;
      }
    }

    return t('common.unexpectedError');
  }

  return (
    <Stack spacing={3}>
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: {
            xs: '1fr',
            md: '1.2fr 0.8fr'
          },
          gap: 3
        }}
      >
        <Card>
          <CardContent sx={{ p: 3 }}>
            <Stack
              direction="row"
              spacing={2}
              sx={{ mb: 3, alignItems: 'center' }}
            >
              <Box
                sx={(theme) => ({
                  width: 48,
                  height: 48,
                  borderRadius: 3,
                  display: 'grid',
                  placeItems: 'center',
                  color: 'primary.main',
                  backgroundColor:
                    theme.palette.mode === 'light'
                      ? '#e8f3ff'
                      : 'rgba(25, 118, 210, 0.18)'
                })}
              >
                <DirectionsCarIcon />
              </Box>

              <Box>
                <Typography variant="h5">
                  {editingCarId ? t('cars.updateTitle') : t('cars.createTitle')}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {t('cars.createDescription')}
                </Typography>
              </Box>
            </Stack>

            <Stack spacing={2.2}>
              {errorMessage && (
                <Alert severity="error" onClose={() => setErrorMessage('')}>
                  {errorMessage}
                </Alert>
              )}

              {successMessage && (
                <Alert severity="success" onClose={() => setSuccessMessage('')}>
                  {successMessage}
                </Alert>
              )}

              <TextField
                label={t('cars.licensePlate')}
                value={licensePlate}
                onChange={(event) => setLicensePlate(event.target.value)}
                placeholder={t('cars.licensePlatePlaceholder')}
                fullWidth
                slotProps={{
                  input: {
                    startAdornment: (
                      <InputAdornment position="start">
                        <BadgeIcon fontSize="small" />
                      </InputAdornment>
                    )
                  }
                }}
              />

              <Box
                sx={{
                  display: 'grid',
                  gridTemplateColumns: {
                    xs: '1fr',
                    sm: '1fr 1fr'
                  },
                  gap: 2
                }}
              >
                <TextField
                  label={t('cars.brand')}
                  value={brand}
                  onChange={(event) => setBrand(event.target.value)}
                  placeholder={t('cars.brandPlaceholder')}
                  fullWidth
                  slotProps={{
                    input: {
                      startAdornment: (
                        <InputAdornment position="start">
                          <FactoryIcon fontSize="small" />
                        </InputAdornment>
                      )
                    }
                  }}
                />

                <TextField
                  label={t('cars.model')}
                  value={model}
                  onChange={(event) => setModel(event.target.value)}
                  placeholder={t('cars.modelPlaceholder')}
                  fullWidth
                  slotProps={{
                    input: {
                      startAdornment: (
                        <InputAdornment position="start">
                          <TimeToLeaveIcon fontSize="small" />
                        </InputAdornment>
                      )
                    }
                  }}
                />
              </Box>

              <Stack direction="row" spacing={1.5}>
                <Button
                  variant="contained"
                  startIcon={<SaveIcon />}
                  onClick={handleSubmit}
                  disabled={!isFormValid() || saving}
                  sx={{ px: 3 }}
                >
                  {saving
                    ? t('common.saving')
                    : editingCarId
                      ? t('cars.updateButton')
                      : t('cars.createButton')}
                </Button>

                {editingCarId && (
                  <Button variant="outlined" onClick={resetForm}>
                    {t('cars.cancel')}
                  </Button>
                )}
              </Stack>
            </Stack>
          </CardContent>
        </Card>

        <Card
          sx={{
            background:
              'linear-gradient(135deg, #0f74d1 0%, #1976d2 45%, #00a6ff 100%)',
            color: 'white',
            overflow: 'hidden',
            position: 'relative'
          }}
        >
          <CardContent sx={{ p: 3, position: 'relative', zIndex: 1 }}>
            <Typography variant="h6" sx={{ opacity: 0.9 }}>
              {t('cars.summaryTitle')}
            </Typography>

            {summaryLoading ? (
              <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mt: 4 }}>
                <CircularProgress size={28} sx={{ color: 'white' }} />
                <Typography variant="body1" sx={{ opacity: 0.9 }}>
                  {t('common.loading')}
                </Typography>
              </Box>
            ) : (
              <>
                <Typography
                  variant="h3"
                  sx={{ mt: 2, fontWeight: 900 }}
                >
                  {summary?.totalCars ?? 0}
                </Typography>

                <Typography variant="body1" sx={{ opacity: 0.9 }}>
                  {t('cars.totalCars')}
                </Typography>

                <Stack spacing={1.2} sx={{ mt: 4 }}>
                  <Chip
                    label={`${summary?.totalServices ?? 0} ${t('cars.totalServices')}`}
                    sx={{
                      color: 'white',
                      backgroundColor: 'rgba(255,255,255,0.18)',
                      width: 'fit-content'
                    }}
                  />

                  <Chip
                    label={`${summary?.pendingServices ?? 0} ${t('cars.pendingServices')}`}
                    sx={{
                      color: 'white',
                      backgroundColor: 'rgba(255,255,255,0.18)',
                      width: 'fit-content'
                    }}
                  />

                  <Chip
                    label={`${summary?.inProgressServices ?? 0} ${t('cars.inProgressServices')}`}
                    sx={{
                      color: 'white',
                      backgroundColor: 'rgba(255,255,255,0.18)',
                      width: 'fit-content'
                    }}
                  />

                  <Chip
                    label={`${summary?.doneServices ?? 0} ${t('cars.doneServices')}`}
                    sx={{
                      color: 'white',
                      backgroundColor: 'rgba(255,255,255,0.18)',
                      width: 'fit-content'
                    }}
                  />
                </Stack>
              </>
            )}
          </CardContent>

          <DirectionsCarIcon
            sx={{
              position: 'absolute',
              right: -20,
              bottom: -20,
              fontSize: 180,
              opacity: 0.12
            }}
          />
        </Card>
      </Box>

      <Card>
        <CardContent sx={{ p: 3 }}>
          <Stack
            direction={{ xs: 'column', sm: 'row' }}
            spacing={2}
            sx={{
              mb: 2,
              justifyContent: 'space-between',
              alignItems: {
                xs: 'flex-start',
                sm: 'center'
              }
            }}
          >
            <Box>
              <Typography variant="h5">
                {t('cars.listTitle')}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {t('cars.listDescription')}
              </Typography>
            </Box>

            <Button
              variant="outlined"
              startIcon={<RefreshIcon />}
              onClick={refreshPageData}
              disabled={loading || summaryLoading}
            >
              {t('cars.refresh')}
            </Button>
          </Stack>

          <Divider sx={{ mb: 2 }} />

          {loading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 6 }}>
              <CircularProgress />
            </Box>
          ) : (
            <Paper
              variant="outlined"
              sx={{
                borderRadius: 3,
                overflow: 'hidden'
              }}
            >
              <TableContainer>
                <Table>
                  <TableHead>
                    <TableRow
                      sx={(theme) => ({
                        backgroundColor:
                          theme.palette.mode === 'light'
                            ? '#f8fafc'
                            : 'rgba(255,255,255,0.04)'
                      })}
                    >
                      <TableCell>{t('cars.table.id')}</TableCell>
                      <TableCell>{t('cars.table.licensePlate')}</TableCell>
                      <TableCell>{t('cars.table.brand')}</TableCell>
                      <TableCell>{t('cars.table.model')}</TableCell>
                      <TableCell>{t('cars.table.createdAt')}</TableCell>
                      <TableCell align="right">{t('cars.table.actions')}</TableCell>
                    </TableRow>
                  </TableHead>

                  <TableBody>
                    {cars.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={6} align="center" sx={{ py: 6 }}>
                          <Stack spacing={1} sx={{ alignItems: 'center' }}>
                            <DirectionsCarIcon color="disabled" sx={{ fontSize: 44 }} />
                            <Typography color="text.secondary">
                              {t('cars.noCars')}
                            </Typography>
                          </Stack>
                        </TableCell>
                      </TableRow>
                    ) : (
                      cars.map((car) => (
                        <TableRow key={car.id} hover>
                          <TableCell>{car.id}</TableCell>

                          <TableCell>
                            <Chip
                              label={car.licensePlate}
                              size="small"
                              color="primary"
                              variant="outlined"
                            />
                          </TableCell>

                          <TableCell>{car.brand}</TableCell>
                          <TableCell>{car.model}</TableCell>
                          <TableCell>{formatDate(car.createdAt)}</TableCell>

                          <TableCell align="right">
                            <Stack
                              direction="row"
                              spacing={1}
                              sx={{ justifyContent: 'flex-end' }}
                            >
                              <Button
                                size="small"
                                variant="outlined"
                                startIcon={<BuildCircleIcon />}
                                onClick={() => onViewServices(car.id)}
                              >
                                {t('cars.viewServices')}
                              </Button>

                              <IconButton
                                color="primary"
                                onClick={() => handleEdit(car)}
                              >
                                <EditIcon />
                              </IconButton>
                            </Stack>
                          </TableCell>
                        </TableRow>
                      ))
                    )}
                  </TableBody>
                </Table>
              </TableContainer>

              <TablePagination
                component="div"
                count={totalElements}
                page={page}
                rowsPerPage={size}
                rowsPerPageOptions={[5, 10, 20]}
                onPageChange={(_, newPage) => setPage(newPage)}
                onRowsPerPageChange={(event) => {
                  setSize(Number(event.target.value));
                  setPage(0);
                }}
              />
            </Paper>
          )}
        </CardContent>
      </Card>

    </Stack>
  );
}

export default CarsPage;
