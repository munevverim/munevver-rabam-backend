import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  Chip,
  CircularProgress,
  Divider,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  Typography
} from '@mui/material';
import HistoryIcon from '@mui/icons-material/History';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import { getAuditLogs } from '../api/auditApi';
import type { AuditLogResponse } from '../types/audit';

function AuditLogsPage() {
  const { t } = useTranslation();

  const [auditLogs, setAuditLogs] = useState<AuditLogResponse[]>([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  async function loadAuditLogs() {
    try {
      setLoading(true);
      setErrorMessage('');

      const result = await getAuditLogs(page, size);

      setAuditLogs(result.content);
      setTotalElements(result.totalElements);
    } catch (error) {
      setErrorMessage(getErrorMessage(error));
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadAuditLogs();
  }, [page, size]);

  function formatDate(value: string | null) {
    if (!value) {
      return '-';
    }

    return new Date(value).toLocaleString('tr-TR');
  }

  function formatPayload(payload: string) {
    if (!payload) {
      return '-';
    }

    try {
      return JSON.stringify(JSON.parse(payload), null, 2);
    } catch {
      return payload;
    }
  }

  function getEventColor(eventType: string): 'primary' | 'success' | 'info' | 'warning' {
    if (eventType.includes('CREATED')) {
      return 'success';
    }

    if (eventType.includes('UPDATED')) {
      return 'info';
    }

    return 'primary';
  }

  function getEntityColor(entityType: string): 'primary' | 'secondary' {
    if (entityType === 'CAR') {
      return 'primary';
    }

    return 'secondary';
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
            <Stack direction="row" spacing={2} sx={{ alignItems: 'center' }}>
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
                <HistoryIcon />
              </Box>

              <Box>
                <Typography variant="h5">
                  {t('auditLogs.title')}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {t('auditLogs.description')}
                </Typography>
              </Box>
            </Stack>

            <Button
              variant="outlined"
              startIcon={<RefreshIcon />}
              onClick={loadAuditLogs}
              disabled={loading}
            >
              {t('auditLogs.refresh')}
            </Button>
          </Stack>

          <Divider sx={{ mb: 2 }} />

          {errorMessage && (
            <Alert severity="error" onClose={() => setErrorMessage('')} sx={{ mb: 2 }}>
              {errorMessage}
            </Alert>
          )}

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
                      <TableCell>{t('auditLogs.table.id')}</TableCell>
                      <TableCell>{t('auditLogs.table.eventType')}</TableCell>
                      <TableCell>{t('auditLogs.table.entityType')}</TableCell>
                      <TableCell>{t('auditLogs.table.entityId')}</TableCell>
                      <TableCell>{t('auditLogs.table.payload')}</TableCell>
                      <TableCell>{t('auditLogs.table.createdAt')}</TableCell>
                    </TableRow>
                  </TableHead>

                  <TableBody>
                    {auditLogs.length === 0 ? (
                      <TableRow>
                        <TableCell colSpan={6} align="center" sx={{ py: 6 }}>
                          <Stack spacing={1} sx={{ alignItems: 'center' }}>
                            <HistoryIcon color="disabled" sx={{ fontSize: 44 }} />
                            <Typography color="text.secondary">
                              {t('auditLogs.noLogs')}
                            </Typography>
                          </Stack>
                        </TableCell>
                      </TableRow>
                    ) : (
                      auditLogs.map((auditLog) => (
                        <TableRow key={auditLog.id} hover>
                          <TableCell>{auditLog.id}</TableCell>

                          <TableCell>
                            <Chip
                              label={auditLog.eventType}
                              size="small"
                              color={getEventColor(auditLog.eventType)}
                              variant="outlined"
                            />
                          </TableCell>

                          <TableCell>
                            <Chip
                              label={auditLog.entityType}
                              size="small"
                              color={getEntityColor(auditLog.entityType)}
                              variant="outlined"
                            />
                          </TableCell>

                          <TableCell>{auditLog.entityId}</TableCell>

                          <TableCell>
                            <Typography
                              component="pre"
                              variant="body2"
                              sx={{
                                m: 0,
                                maxWidth: 520,
                                maxHeight: 120,
                                overflow: 'auto',
                                whiteSpace: 'pre-wrap',
                                fontFamily: 'monospace',
                                fontSize: 12
                              }}
                            >
                              {formatPayload(auditLog.payload)}
                            </Typography>
                          </TableCell>

                          <TableCell>{formatDate(auditLog.createdAt)}</TableCell>
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

export default AuditLogsPage;