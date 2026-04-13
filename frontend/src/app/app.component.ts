import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ApiService } from './services/api.service';
import { HealthResponse } from './models/health.model';
import { ConfigResponse } from './models/config.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatChipsModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  readonly _health = signal<HealthResponse | null>(null);
  readonly _config = signal<ConfigResponse | null>(null);
  readonly _healthLoading = signal(false);
  readonly _configLoading = signal(false);
  readonly _healthError = signal<string | null>(null);
  readonly _configError = signal<string | null>(null);

  constructor(private readonly api: ApiService) {}

  callHealth(): void {
    this._health.set(null);
    this._healthError.set(null);
    this._healthLoading.set(true);
    this.api.getHealth().subscribe({
      next: (r) => { this._health.set(r); this._healthLoading.set(false); },
      error: (e) => { this._healthError.set(e.message); this._healthLoading.set(false); },
    });
  }

  callConfig(): void {
    this._config.set(null);
    this._configError.set(null);
    this._configLoading.set(true);
    this.api.getConfig().subscribe({
      next: (r) => { this._config.set(r); this._configLoading.set(false); },
      error: (e) => { this._configError.set(e.message); this._configLoading.set(false); },
    });
  }
}
