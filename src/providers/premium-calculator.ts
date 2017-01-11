import { Injectable } from '@angular/core';
import { Headers, Http, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import 'rxjs/add/operator/map';
import {PremiumInfo} from "../models/premium-info";

@Injectable()
export class PremiumCalculator {

  premiumInfoApiUrl: string = "";
  objectRecogniserApiUrl: string = "http://104.131.115.2:9090/imageClassification";

  constructor(public http: Http) {
  }

  getObjectDetails(payLoad:any = null): Observable<string> {

    let headers = new Headers(
      {
        'Content-Type': "text/plain;charset=UTF-8"
      }
    );

    let options = new RequestOptions({headers: headers});
    var body:string = typeof (payLoad) == "string" ? payLoad : JSON.stringify(payLoad);

    //noinspection TypeScriptUnresolvedFunction
    return this.http.post(`${this.objectRecogniserApiUrl}`, body, options)
      .map(res => <any>res.text());
  }

  getPremiumInfo(payLoad:any = null): Observable<any[]> {

    var body:string = typeof (payLoad) == "string" ? payLoad : JSON.stringify(payLoad);
    //noinspection TypeScriptUnresolvedFunction
    return this.http.get(`${this.premiumInfoApiUrl}/premiumcalculator?object=?` + body)
      .map(res => <any[]>res.json());
  }

  getMockObjectDetails(): string {
     return "iPhone";
  }

  getMockPremiumInfo(object:any = null): PremiumInfo {

    var premiumInfo: PremiumInfo;

    switch (object) {

      case "laptop":
        premiumInfo = new PremiumInfo(object, "$600", "$60");
        break;
      case "bag":
        premiumInfo = new PremiumInfo(object, "$30", "$3");
        break;
      case "water":
        premiumInfo = new PremiumInfo(object, "$2", "$0.2");
        break;
      case "mobile phone":
        premiumInfo = new PremiumInfo(object, "$200", "$20");
        break;
      case "pen":
        premiumInfo = new PremiumInfo(object, "$50", "$5");
        break;
      case "furniture":
        premiumInfo = new PremiumInfo(object, "$700", "$65");
        break;
      case "watch":
        premiumInfo = new PremiumInfo(object, "$180", "$18");
        break;
      case "product":
        premiumInfo = new PremiumInfo(object, "NA", "NA");
        break;
      default:
        premiumInfo = new PremiumInfo("Object", "NA", "NA");
        break;
    }

    return premiumInfo;
  }
}
