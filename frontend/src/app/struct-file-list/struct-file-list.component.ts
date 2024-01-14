import { Component } from '@angular/core';
import { faFolder, faFile } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-struct-file-list',
  templateUrl: './struct-file-list.component.html',
  styleUrls: ['./struct-file-list.component.css']
})
export class StructFileListComponent {
  faFolder = faFolder;
  // faFolder = faFile;
}
