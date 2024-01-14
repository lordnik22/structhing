import { Component } from '@angular/core';
import { animate, state, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-loader',
  templateUrl: './loader.component.html',
  styleUrls: ['./loader.component.css'],
  animations: [
    trigger('fade', [
      state('visible', style({ opacity: 1 })),
      state('hidden', style({ opacity: 0, pointerEvents: 'none' })),
      transition('visible <=> hidden', animate('.3s ease-in-out')),
    ]),
  ],
})

export class LoaderComponent {
  initialized = false;

  ngOnInit() {
    this.loadPaths();
  }

  fadeOut() {
    this.initialized = this.initialized ? false : true;
  }

  loadPaths() {
    setTimeout(this.fadeOut.bind(this), 500);
  }
}
