import { v4 as uuidv4 } from 'uuid';
import { ProcessWatchPathType } from './ProcessWatchPathType';

export class StructWatchPath {

    constructor(public id: uuidv4,
        public directoryPath: String,
        public initalProcessFlag: Boolean,
        public createTimestamp: Number,
        public strategyType: ProcessWatchPathType) { }

}