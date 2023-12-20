import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';


@Component({
  selector: 'app-content-area',
  templateUrl: './content-area.component.html',
  styleUrls: ['./content-area.component.css']
})
export class ContentAreaComponent implements OnInit {
  navLinks: { label: string, link: string, index: number }[];

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.navLinks = [
      {
        label: 'Material',
        link: 'material',
        index: 0
      },
      {
        label: 'Is',
        link: 'is',
        index: 1
      },
      {
        label: 'Working',
        link: 'working',
        index: 2
      },
    ]
  }
}