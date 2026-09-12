import { Component, input } from '@angular/core';

/**
 * The sort arrow. Always rendered so the header label never shifts, dimmed until its column is the
 * sorted one, and rotated instead of swapped so there is one shape and not two to keep in step.
 *
 * <p>Template and styles are inline: this component is a single element.
 */
@Component({
  selector: 'app-sort-indicator',
  template: `
    <svg viewBox="0 0 10 6" width="10" height="6" aria-hidden="true">
      <path
        d="M1 5 L5 1 L9 5"
        fill="none"
        stroke="currentColor"
        stroke-width="1.6"
        stroke-linecap="round"
        stroke-linejoin="round"
      />
    </svg>
  `,
  styles: `
    :host {
      display: inline-flex;
      flex-shrink: 0;
      color: #cbd2dc;
      transition: transform 0.15s ease;
    }

    :host(.active) {
      color: #1f2937;
    }

    :host(.descending) {
      transform: rotate(180deg);
    }
  `,
  host: {
    '[class.active]': 'active()',
    '[class.descending]': 'descending()',
  },
})
export class SortIndicator {
  readonly active = input(false);
  readonly descending = input(false);
}
