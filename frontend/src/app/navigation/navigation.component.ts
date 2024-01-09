import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { NgFor } from '@angular/common';
import { MatListModule } from '@angular/material/list';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css'],
  imports: [MatListModule, NgFor, RouterModule],
  standalone: true
})
export class NavigationComponent {

  // constructor(private router: Router) { }

  // ngOnInit(): void {
  //   this.navLinks = [
  //     {
  //       label: 'Verzeichnisse',
  //       link: 'paths',
  //       index: 0
  //     },
  //     {
  //       label: 'Tags',
  //       link: 'tags',
  //       index: 1
  //     },
  //     {
  //       label: 'Suche',
  //       link: 'search',
  //       index: 2
  //     },
  //     {
  //       label: 'Logs',
  //       link: 'logs',
  //       index: 2
  //     },
  //   ]
  // }
}
