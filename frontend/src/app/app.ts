import { Component, inject } from '@angular/core';
import { WorkationsTable } from './components/workations-table/workations-table';
import { WorkationService } from './services/workation';

/** The smart component: it owns the data source and the loading and error states. */
@Component({
  selector: 'app-root',
  imports: [WorkationsTable],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private readonly workationService = inject(WorkationService);

  protected readonly workations = this.workationService.workations;
}
