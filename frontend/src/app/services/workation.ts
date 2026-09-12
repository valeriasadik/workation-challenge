import { httpResource } from '@angular/common/http';
import { Service } from '@angular/core';
import { environment } from '../../environments/environment';
import { Workation, WorkationResponse } from '../models/workation';

/** Wire shape to model: the two date strings become Dates, every other field travels as it is. */
export function toWorkation(response: WorkationResponse): Workation {
  return { ...response, start: new Date(response.start), end: new Date(response.end) };
}

@Service()
export class WorkationService {
  /** `defaultValue` keeps value() a plain array, so templates never need `?? []`. */
  readonly workations = httpResource<Workation[]>(
    () => `${environment.apiBaseUrl}/workation`,
    {
      parse: (raw) => (raw as WorkationResponse[]).map(toWorkation),
      defaultValue: [],
    },
  );
}
