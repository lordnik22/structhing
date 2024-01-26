import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StructPathListComponent } from './struct-path-list.component';

describe('StructPathListComponent', () => {
  let component: StructPathListComponent;
  let fixture: ComponentFixture<StructPathListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StructPathListComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StructPathListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
