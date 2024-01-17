// import { Injectable } from '@angular/core';

// @Injectable({
//   providedIn: 'root'
// })
// export class DirectoryPickerService {
//   async pickDirectory(): Promise<string | undefined> {
//     const directoryHandle = await window.showDirectoryPicker();
//     const relativePaths = await directoryHandle.resolve(directoryHandle);
//     console.log(relativePaths);
//     return directoryHandle?.name;
//   }
// }
// directory-picker.service.ts

import { Injectable } from '@angular/core';
const { remote } = require('electron');

@Injectable({
  providedIn: 'root'
})
export class DirectoryPickerService {
  async pickDirectory(): Promise<string | undefined> {
    const result = await remote.dialog.showOpenDialog({
      properties: ['openDirectory']
    });

    if (!result.canceled && result.filePaths.length > 0) {
      return result.filePaths[0];
    }

    return undefined;
  }
}
