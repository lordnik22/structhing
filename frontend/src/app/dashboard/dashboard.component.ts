import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';
import { faFolder } from '@fortawesome/free-solid-svg-icons';
import { v4 as uuidv4 } from 'uuid';
import { ProcessWatchPathType } from '../shared/model/ProcessWatchPathType';
import { StructWatchPath } from '../shared/model/StructWatchPath.model';

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

    this.http.get(
      this.baseURL + '/api/watch/path/all',
      {headers: headers}
    )
    .subscribe((res) => {
      // TODO: Add directories from res to pathLists like below
      this.pathLists = [
        {
          directory: {
              id: 'c875afc6-e949-4c51-a71f-e086b7b379d9',
              createDate: '2023-12-17 17:12',
              directoryPath: 'C:\\Users\\johndoe\\Downloads',
              fileSize: '—',
              fileType: faFolder
            },
        },
        {
          directory: {
              id: 'cd93a627-22c2-49e6-8b9c-c99a67fce137',
              createDate: '2023-12-17 17:12',
              directoryPath: 'C:\\Users\\johndoe\\Downloads',
              fileSize: '—',
              fileType: faFolder
            },
        },
      ];

      this.loadingStatus = false;
    });
  }

  addPath(path: string) {
    this.http.put(
      this.baseURL + '/api/watch/path',
      new StructWatchPath(uuidv4(), path, true, Date.now(), ProcessWatchPathType.PDF_ONLY)
    )
    .subscribe((res) => {
      this.getAllPaths();
    });
  }

  deletePath(uuid: string) {
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
    const skeletonEntry = {
      directory: {
          id: 'dummy',
          createDate: '0000-00-00 00:00',
          directoryPath: '...',
          fileSize: '—',
          fileType: faFolder
        },
    };
    const skeletonArray = new Array(10).fill(skeletonEntry);
    this.pathLists = skeletonArray;
  }
}
