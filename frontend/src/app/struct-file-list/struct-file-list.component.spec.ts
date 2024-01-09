import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StructFileListComponent } from './struct-file-list.component';

describe('StructFileListComponent', () => {
  let component: StructFileListComponent;
  let fixture: ComponentFixture<StructFileListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StructFileListComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StructFileListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
