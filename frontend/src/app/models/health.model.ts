export interface HealthResponse {
  status: 'ok' | 'degraded';
  version: string;
  uptime: number;
  reason?: string;
}
