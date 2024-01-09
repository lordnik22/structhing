import { Component } from '@angular/core';
import packageJson from '../../package.json';

@Component({
  selector: 'app-root',
  styleUrls: ['./app.component.css'],
  templateUrl: './app.component.html',
})

export class AppComponent {
  public appTitle: string = packageJson.name;
  public appVersion: string = packageJson.version;
}
