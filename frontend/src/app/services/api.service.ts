import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { HealthResponse } from '../models/health.model';
import { ConfigResponse } from '../models/config.model';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  getHealth(): Observable<HealthResponse> {
    return this.http.get<HealthResponse>('/health');
  }

  getConfig(): Observable<ConfigResponse> {
    return this.http.get<ConfigResponse>('/config');
  }
}
