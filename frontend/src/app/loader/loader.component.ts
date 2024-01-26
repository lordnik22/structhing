import { Component, Input } from '@angular/core';
import { animate, state, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-loader',
  templateUrl: './loader.component.html',
  styleUrls: ['./loader.component.css'],
  animations: [
    trigger('fade', [
      state('visible', style({ opacity: 1 })),
      state('hidden', style({ opacity: 0 })),
      transition('visible <=> hidden', animate('.3s ease-in-out')),
    ]),
  ],
})

export class LoaderComponent {
  @Input() isLoading: boolean;

  // ngOnInit() {
  //   this.loadPaths();
  // }

  // fadeOut() {
  //   this.isLoading = this.isLoading ? false : true;
  // }

  // loadPaths() {
  //   setTimeout(this.fadeOut.bind(this), 500);
  // }
}
