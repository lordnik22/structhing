import { Component, Inject } from '@angular/core';
import {
  MatDialog,
  MAT_DIALOG_DATA,
  MatDialogRef,
  MatDialogTitle,
  MatDialogContent,
  MatDialogActions,
  MatDialogClose,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { DirectoryPickerService } from './directory-picker.service';

export interface DialogData {
  createDate: string;
  directoryPath: string;
}

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss'],
  standalone: true,
  imports: [MatFormFieldModule, MatInputModule, FormsModule, MatButtonModule, MatIconModule]
})
export class DialogComponent {
  animal: string;
  name: string;

  constructor(public dialog: MatDialog) {}

  openDirectoryPicker(): void {
    const dialogRef = this.dialog.open(DirectoryPickerDialogComponent);

    dialogRef.afterClosed().subscribe((selectedDirectory: string | undefined) => {
      if (selectedDirectory) {
        console.log('Selected directory:', selectedDirectory);
        // Do something with the selected directory path
      }
    });
  }
}

@Component({
  selector: 'app-directory-picker-dialog',
  standalone: true,
  imports: [
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    MatButtonModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogClose,
  ],
  template: `
    <h2 mat-dialog-title>Choose Directory</h2>
    <mat-dialog-content>
      <p>Select a directory:</p>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-button (click)="cancel()">Cancel</button>
      <button mat-button (click)="selectDirectory()">Select Directory</button>
    </mat-dialog-actions>
  `
})
export class DirectoryPickerDialogComponent {
  constructor(
    private dialogRef: MatDialogRef<DirectoryPickerDialogComponent>,
    private directoryPickerService: DirectoryPickerService
  ) {}

  cancel(): void {
    this.dialogRef.close();
  }

  async selectDirectory(): Promise<void> {
    const directory = await this.directoryPickerService.pickDirectory();
    if (directory) {
      this.dialogRef.close(directory);
    }
  }

  // async change(event): Promise<void> {
  //   console.log(event);
  // }
}
