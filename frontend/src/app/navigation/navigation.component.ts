import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { NgFor } from '@angular/common';
import { MatListModule } from '@angular/material/list';
import packageJson from '../../../package.json';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css'],
  imports: [MatListModule, NgFor, RouterModule],
  standalone: true
})
export class NavigationComponent {
  public appTitle: string = packageJson.name;
  public appVersion: string = packageJson.version;
}
