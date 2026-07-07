import {
  Alert,
  Autocomplete,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Divider,
  FormControl,
  InputLabel,
  MenuItem,
  Paper,
  Select,
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
import BuildCircleIcon from '@mui/icons-material/BuildCircle';
import RefreshIcon from '@mui/icons-material/Refresh';
import SaveIcon from '@mui/icons-material/Save';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import { getCars } from '../api/carApi';
import { getDashboardSummary } from '../api/dashboardApi';
import {
  createService,
  getServices,
  updateService
} from '../api/serviceApi';
import type { CarResponse } from '../types/car';
import type { DashboardSummaryResponse } from '../types/dashboard';
import type {
  ServiceRequest,
  ServiceResponse,
  ServiceStatus
} from '../types/service';

const SERVICE_TITLE_OPTIONS = [
  'Bakım',
  'Muayene',
  'Araç Yıkama',
  'Lastik',
  'Akaryakıt',
  'Ekspertiz',
  'Çekici',
  'Sigorta'
];

function ServicesPage() {
  const { t } = useTranslation();

  const [cars, setCars] = useState<CarResponse[]>([]);
  const [services, setServices] = useState<ServiceResponse[]>([]);
  const [summary, setSummary] = useState<DashboardSummaryResponse | null>(null);

  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [selectedCar, setSelectedCar] = useState<CarResponse | null>(null);

  const [filterCar, setFilterCar] = useState<CarResponse | null>(null);
  const [filterStatus, setFilterStatus] = useState<ServiceStatus | ''>('');

  const [appliedFilterCar, setAppliedFilterCar] = useState<CarResponse | null>(null);
  const [appliedFilterStatus, setAppliedFilterStatus] = useState<ServiceStatus | ''>('');

  const [loading, setLoading] = useState(false);
  const [carsLoading, setCarsLoading] = useState(false);
  const [summaryLoading, setSummaryLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [updatingServiceId, setUpdatingServiceId] = useState<number | null>(null);

  const [errorMessage, setErrorMessage] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  async function loadCarsForDropdown() {
    try {
      setCarsLoading(true);
      setErrorMessage('');

      const result = await getCars(0, 100);

      setCars(result.content);
    } catch (error) {
      setErrorMessage(getErrorMessage(error));
    } finally {
      setCarsLoading(false);
    }
  }

  async function loadServices() {
    try {
      setLoading(true);
      setErrorMessage('');

      const result = await getServices(page, size, {
        carId: appliedFilterCar ? appliedFilterCar.id : '',
        status: appliedFilterStatus
      });

      setServices(result.content);
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
    await loadServices();
    await loadSummary();
  }

  useEffect(() => {
    loadCarsForDropdown();
    loadSummary();
  }, []);

  useEffect(() => {
    loadServices();
  }, [page, size, appliedFilterCar, appliedFilterStatus]);

  async function handleCreateService() {
    try {
      setSaving(true);
      setErrorMessage('');
      setSuccessMessage('');

      if (!selectedCar) {
        setErrorMessage(t('services.selectCarRequired'));
        return;
      }

      const request: ServiceRequest = {
        title,
        description,
        carId: selectedCar.id
      };

      await createService(request);

      setSuccessMessage(t('services.created'));
      resetCreateForm();
      setPage(0);

      await loadServices();
      await loadSummary();
    } catch (error) {
      setErrorMessage(getErrorMessage(error));
    } finally {
      setSaving(false);
    }
  }

  async function handleStatusChange(service: ServiceResponse, nextStatus: ServiceStatus) {
    try {
      setUpdatingServiceId(service.id);
      setErrorMessage('');
      setSuccessMessage('');

      await updateService(service.id, {
        status: nextStatus,
        version: service.version
      });

      setSuccessMessage(t('services.updated'));

      await loadServices();
      await loadSummary();
    } catch (error) {
      setErrorMessage(getErrorMessage(error));
    } finally {
      setUpdatingServiceId(null);
    }
  }

  function handleApplyFilters() {
    setAppliedFilterCar(filterCar);
    setAppliedFilterStatus(filterStatus);
    setPage(0);
  }

  function handleClearFilters() {
    setFilterCar(null);
    setFilterStatus('');
    setAppliedFilterCar(null);
    setAppliedFilterStatus('');
    setPage(0);
  }

  function resetCreateForm() {
    setTitle('');
    setDescription('');
    setSelectedCar(null);
  }

  function isCreateFormValid() {
    return title.trim() !== '' && selectedCar !== null;
  }

  function getNextStatuses(status: ServiceStatus): ServiceStatus[] {
    if (status === 'PENDING') {
      return ['IN_PROGRESS'];
    }

    if (status === 'IN_PROGRESS') {
      return ['DONE'];
    }

    return [];
  }

  function getStatusColor(status: ServiceStatus): 'warning' | 'info' | 'success' {
    if (status === 'PENDING') {
      return 'warning';
    }

    if (status === 'IN_PROGRESS') {
      return 'info';
    }

    return 'success';
  }

  function formatDate(value: string | null) {
    if (!value) {
      return '-';
    }

    return new Date(value).toLocaleString('tr-TR');
  }

  function getCarOptionLabel(car: CarResponse) {
    return `${car.licensePlate} — ${car.brand} ${car.model}`;
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
            md: '1.15fr 0.85fr'
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
                <BuildCircleIcon />
              </Box>

              <Box>
                <Typography variant="h5">
                  {t('services.createTitle')}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {t('services.createDescription')}
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

              <FormControl fullWidth>
                <InputLabel>{t('services.title')}</InputLabel>
                <Select
                  label={t('services.title')}
                  value={title}
                  onChange={(event) => setTitle(event.target.value)}
                >
                  <MenuItem value="" disabled>
                    {t('services.titlePlaceholder')}
                  </MenuItem>
                  {SERVICE_TITLE_OPTIONS.map((serviceTitle) => (
                    <MenuItem key={serviceTitle} value={serviceTitle}>
                      {serviceTitle}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>

              <TextField
                label={t('services.description')}
                value={description}
                onChange={(event) => setDescription(event.target.value)}
                placeholder={t('services.descriptionPlaceholder')}
                fullWidth
                multiline
                minRows={3}
              />

              <Autocomplete
                options={cars}
                value={selectedCar}
                onChange={(_, newValue) => setSelectedCar(newValue)}
                loading={carsLoading}
                getOptionLabel={getCarOptionLabel}
                isOptionEqualToValue={(option, value) => option.id === value.id}
                noOptionsText={t('cars.noCars')}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label={t('services.selectCar')}
                    placeholder={t('services.selectCarPlaceholder')}
                    fullWidth
                  />
                )}
              />

              <Button
                variant="contained"
                startIcon={<SaveIcon />}
                onClick={handleCreateService}
                disabled={!isCreateFormValid() || saving}
                sx={{ width: 'fit-content', px: 3 }}
              >
                {saving ? t('common.saving') : t('services.createButton')}
              </Button>
            </Stack>
          </CardContent>
        </Card>

        <Card
          sx={{
            background:
              'linear-gradient(135deg, #263238 0%, #1976d2 55%, #00a6ff 100%)',
            color: 'white',
            overflow: 'hidden',
            position: 'relative'
          }}
        >
          <CardContent sx={{ p: 3, position: 'relative', zIndex: 1 }}>
            <Typography variant="h6" sx={{ opacity: 0.9 }}>
              {t('services.summaryTitle')}
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
                  {summary?.totalServices ?? 0}
                </Typography>

                <Typography variant="body1" sx={{ opacity: 0.9 }}>
                  {t('services.totalServices')}
                </Typography>

                <Stack spacing={1.2} sx={{ mt: 3 }}>
                  <Chip
                    label={`${summary?.pendingServices ?? 0} ${t('services.pendingServices')}`}
                    sx={{
                      color: 'white',
                      backgroundColor: 'rgba(255,255,255,0.18)',
                      width: 'fit-content'
                    }}
                  />

                  <Chip
                    label={`${summary?.inProgressServices ?? 0} ${t('services.inProgressServices')}`}
                    sx={{
                      color: 'white',
                      backgroundColor: 'rgba(255,255,255,0.18)',
                      width: 'fit-content'
                    }}
                  />

                  <Chip
                    label={`${summary?.doneServices ?? 0} ${t('services.doneServices')}`}
                    sx={{
                      color: 'white',
                      backgroundColor: 'rgba(255,255,255,0.18)',
                      width: 'fit-content'
                    }}
                  />
                </Stack>

                <Divider sx={{ my: 2.5, borderColor: 'rgba(255,255,255,0.25)' }} />

                <Typography variant="h6" sx={{ opacity: 0.95 }}>
                  {t('services.statusFlow')}
                </Typography>

                <Stack spacing={1.1} sx={{ mt: 1.5 }}>
                  <Chip
                    label={t('services.rulePending')}
                    sx={{
                      color: 'white',
                      backgroundColor: 'rgba(255,255,255,0.18)',
                      width: 'fit-content'
                    }}
                  />

                  <Chip
                    label={t('services.ruleDone')}
                    sx={{
                      color: 'white',
                      backgroundColor: 'rgba(255,255,255,0.18)',
                      width: 'fit-content'
                    }}
                  />

                  <Chip
                    label={t('services.ruleMaxActive')}
                    sx={{
                      color: 'white',
                      backgroundColor: 'rgba(255,255,255,0.18)',
                      width: 'fit-content'
                    }}
                  />

                  <Chip
                    label={t('services.ruleOptimistic')}
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

          <BuildCircleIcon
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
                {t('services.listTitle')}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {t('services.listDescription')}
              </Typography>
            </Box>

            <Button
              variant="outlined"
              startIcon={<RefreshIcon />}
              onClick={refreshPageData}
              disabled={loading || summaryLoading}
            >
              {t('services.refresh')}
            </Button>
          </Stack>

          <Divider sx={{ mb: 2 }} />

          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: {
                xs: '1fr',
                md: '1fr 1fr auto auto'
              },
              gap: 2,
              mb: 2
            }}
          >
            <Autocomplete
              options={cars}
              value={filterCar}
              onChange={(_, newValue) => setFilterCar(newValue)}
              loading={carsLoading}
              getOptionLabel={getCarOptionLabel}
              isOptionEqualToValue={(option, value) => option.id === value.id}
              noOptionsText={t('cars.noCars')}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label={t('services.filterByCar')}
                  placeholder={t('services.allCars')}
                  fullWidth
                />
              )}
            />

            <FormControl size="small" fullWidth>
              <InputLabel>{t('services.status')}</InputLabel>
              <Select
                label={t('services.status')}
                value={filterStatus}
                onChange={(event) => setFilterStatus(event.target.value as ServiceStatus | '')}
              >
                <MenuItem value="">{t('services.allStatuses')}</MenuItem>
                <MenuItem value="PENDING">PENDING</MenuItem>
                <MenuItem value="IN_PROGRESS">IN_PROGRESS</MenuItem>
                <MenuItem value="DONE">DONE</MenuItem>
              </Select>
            </FormControl>

            <Button variant="contained" onClick={handleApplyFilters}>
              {t('services.filter')}
            </Button>

            <Button variant="outlined" onClick={handleClearFilters}>
              {t('services.clear')}
            </Button>
          </Box>

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
                      <TableCell>{t('services.table.id')}</TableCell>
                      <TableCell>{t('services.table.title')}</TableCell>
                      <TableCell>{t('services.table.car')}</TableCell>
                      <TableCell>{t('services.table.status')}</TableCell>
                      <TableCell>{t('services.table.version')}</TableCell>
                      <TableCell>{t('services.table.createdAt')}</TableCell>
                      <TableCell align="right">{t('services.table.nextStatus')}</TableCell>
                    </TableRow>
                  </TableHead>

                  <TableBody>
                    {services.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={7} align="center" sx={{ py: 6 }}>
                          <Stack spacing={1} sx={{ alignItems: 'center' }}>
                            <BuildCircleIcon color="disabled" sx={{ fontSize: 44 }} />
                            <Typography color="text.secondary">
                              {t('services.noServices')}
                            </Typography>
                          </Stack>
                        </TableCell>
                      </TableRow>
                    ) : (
                      services.map((service) => {
                        const nextStatuses = getNextStatuses(service.status);

                        return (
                          <TableRow key={service.id} hover>
                            <TableCell>{service.id}</TableCell>

                            <TableCell>
                              <Typography sx={{ fontWeight: 700 }}>
                                {service.title}
                              </Typography>
                              <Typography variant="body2" color="text.secondary">
                                {service.description || '-'}
                              </Typography>
                            </TableCell>

                            <TableCell>
                              <Stack
                                direction="row"
                                spacing={1.2}
                                sx={{ alignItems: 'center' }}
                              >
                                <DirectionsCarIcon color="primary" fontSize="small" />
                                <Box>
                                  <Typography sx={{ fontWeight: 700 }}>
                                    {service.carLicensePlate}
                                  </Typography>
                                  <Typography variant="body2" color="text.secondary">
                                    {service.carBrand} {service.carModel}
                                  </Typography>
                                </Box>
                              </Stack>
                            </TableCell>

                            <TableCell>
                              <Chip
                                label={service.status}
                                size="small"
                                color={getStatusColor(service.status)}
                              />
                            </TableCell>

                            <TableCell>{service.version}</TableCell>
                            <TableCell>{formatDate(service.createdAt)}</TableCell>

                            <TableCell align="right">
                              {nextStatuses.length === 0 ? (
                                <Chip
                                  label={t('services.completed')}
                                  size="small"
                                  color="success"
                                />
                              ) : (
                                <Stack
                                  direction="row"
                                  spacing={1}
                                  sx={{ justifyContent: 'flex-end' }}
                                >
                                  {nextStatuses.map((nextStatus) => (
                                    <Button
                                      key={nextStatus}
                                      size="small"
                                      variant="outlined"
                                      disabled={updatingServiceId === service.id}
                                      onClick={() => handleStatusChange(service, nextStatus)}
                                    >
                                      {updatingServiceId === service.id
                                        ? t('common.updating')
                                        : nextStatus}
                                    </Button>
                                  ))}
                                </Stack>
                              )}
                            </TableCell>
                          </TableRow>
                        );
                      })
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

export default ServicesPage;
