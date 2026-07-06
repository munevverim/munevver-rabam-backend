export type AuditLogResponse = {
  id: number;
  eventType: string;
  entityType: string;
  entityId: number;
  payload: string;
  createdAt: string;
};