import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';
import { AlertModel } from 'app/shared/models/alert';

@Injectable()
export class AlertService {
  public alertTriggered = new Subject<AlertModel>();

  triggerAlert(alert: AlertModel) {
    this.alertTriggered.next(alert);
  }

  getAlert(): Observable<AlertModel> {
    return this.alertTriggered.asObservable();
  }
}