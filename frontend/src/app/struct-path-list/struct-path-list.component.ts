import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IconDefinition, Icon } from '@fortawesome/fontawesome-svg-core';
import { faFolder, faFile } from '@fortawesome/free-solid-svg-icons';

interface IStructPath {
  id: string,
  createDate: string,
  directoryPath: string,
  fileSize: string,
  fileType: IconDefinition,
}

@Component({
  selector: 'app-struct-path-list',
  templateUrl: './struct-path-list.component.html',
  styleUrls: ['./struct-path-list.component.css'],
})
export class StructPathListComponent {
  @Input() pathList: IStructPath;
  @Output() messageEvent = new EventEmitter<string>();

  delete(id) {
    this.messageEvent.emit(id);
  }
}
