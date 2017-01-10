import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/map';

@Injectable()
export class PremiumCalculator {

  premiumInfoApiUrl: string = "";

  constructor(public http: Http) {
    console.log('Hello PremiumCalculator Provider');
  }

  load(): Observable<any[]> {
    //noinspection TypeScriptUnresolvedFunction
    return this.http.get(`${this.premiumInfoApiUrl}/users`)
      .map(res => <any[]>res.json());
  }

}
