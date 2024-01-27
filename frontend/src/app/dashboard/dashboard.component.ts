import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { faFolder } from '@fortawesome/free-solid-svg-icons';
import { v4 as uuidv4 } from 'uuid';
import { ProcessWatchPathType } from '../shared/model/ProcessWatchPathType';
import { StructWatchPath } from '../shared/model/StructWatchPath.model';
import { map } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  baseURL = 'http://localhost:7070';
  loadingStatus: boolean = true;
  pathLists: any[] = [];
  message: string;

  constructor(private http: HttpClient) {
    this.loadSkeletonData();
    this.getAllPaths();
  }

  ngOnInit() {
    // TODO: Remove me when loading works
    setTimeout(() => {
      this.loadingStatus = false;
    }, 500);
  }

  getAllPaths() {
    const headers = new HttpHeaders();
    this.loadingStatus = true;

    this.http.get(
      this.baseURL + '/api/watch/path/all',
      {headers: headers}
    )
    .subscribe((res: any[]) => {
      this.pathLists = [];

      res.forEach(element => {
        this.pathLists.push({
          id: element.id,
          createDate: element.createTimestamp,
          directoryPath: element.directoryPath,
          fileSize: '—',
          fileType: faFolder,
        });
      });

      this.loadingStatus = false;
    });
  }

  addPath(path: string) {
    this.loadingStatus = true;

    this.http.put(
      this.baseURL + '/api/watch/path',
      new StructWatchPath(uuidv4(), path, true, Date.now(), ProcessWatchPathType.PDF_ONLY)
    )
    .subscribe((res) => {
      this.getAllPaths();
    });
  }

  deletePath(uuid: string) {
    this.loadingStatus = true;

    this.http.delete(
      this.baseURL + '/api/watch/path/' + uuid
    )
    .subscribe((res) => {
      this.getAllPaths();
    });
  }

  openDialog() {
    // console.log('test');
  }

  receiveAddMessage($event) {
    this.message = $event;
    this.addPath(this.message);
  }

  receiveDeleteMessage($event) {
    this.message = $event;
    this.deletePath(this.message);
  }

  loadSkeletonData() {
    this.pathLists = new Array(3).fill({
      id: 'dummy',
      createDate: '0000-00-00 00:00',
      directoryPath: '...',
      fileSize: '—',
      fileType: faFolder
    });
  }
}
