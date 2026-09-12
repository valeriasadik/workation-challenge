import { Component, computed, input } from '@angular/core';
import { Country } from '../../models/workation';

/** An image, not a flag emoji: Windows does not render country flag glyphs. */
const FLAG_CDN = 'https://flagcdn.com';

@Component({
  selector: 'app-country-cell',
  templateUrl: './country-cell.html',
  styleUrl: './country-cell.scss',
})
export class CountryCell {
  readonly country = input.required<Country>();

  protected readonly flagUrl = computed(
    () => `${FLAG_CDN}/${this.country().isoCode.toLowerCase()}.svg`,
  );
}
