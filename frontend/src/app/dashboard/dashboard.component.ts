import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {
  baseURL = 'http://localhost:7070';
  pathLists = [];
  constructor(private http: HttpClient) {
    this.pathLists = [1,2,3,4,5];
    // this.getAllPaths();
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
    console.log(path);
    const headers = new HttpHeaders();

    this.http.put(
      this.baseURL + '/api/watch/path',
      path, {headers: headers}
    )
    .subscribe((res) => {
      console.log(res);
    });
  }

  openDialog() {
    console.log('test');
  }
}
