import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, ViewChild } from '@angular/core';
import { DialogComponent } from '../dialog/dialog.component';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  baseURL = 'http://localhost:7070';
  loadingStatus: boolean = true;
  pathLists = [];
  message: string;

  constructor(private http: HttpClient) {
    this.pathLists = [1,2,3,4,5];
    // this.getAllPaths();
  }

  ngOnInit() {
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
      console.log(res);
      console.log(this.pathLists);
    });
  }

  addPath(path: string) {
    const headers = new HttpHeaders();

    this.http.put(
      this.baseURL + '/api/watch/path',
      path, {headers: headers}
    )
    .subscribe((res) => {
      this.getAllPaths();
    });
  }

  openDialog() {
    console.log('test');
  }

  receiveMessage($event) {
    this.message = $event;
    this.addPath(this.message);
  }
}
