import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';
import {Camera} from 'ionic-native';

import { PremiumCalculator } from '../../providers/premium-calculator';
import { PremiumInfo } from '../../models/premium-info';

@Component({
  selector: 'page-premium-info',
  templateUrl: 'premium-info.html'
})
export class PremiumInfoPage {

  public base64Image: string;
  public byteArray: string;
  public objectDetails: string;
  public premiumInfo: PremiumInfo;

  constructor(public navCtrl: NavController, public navParams: NavParams,
              private premiumCalculator: PremiumCalculator) {}

  takePicture() {
    //noinspection TypeScriptUnresolvedVariable
    Camera.getPicture({
      destinationType: Camera.DestinationType.DATA_URL,
      targetWidth: 1000,
      targetHeight: 1000
    })
    .then((imageData) => {

      // imageData is a base64 encoded string
      this.base64Image = "data:image/jpeg;base64," + imageData;
      this.byteArray = imageData;

      this.premiumCalculator.getObjectDetails(imageData)
        .subscribe(objectDetails => {
          this.objectDetails = objectDetails;
          this.premiumInfo = this.premiumCalculator.getMockPremiumInfo(this.objectDetails);
        });

      // Mock Service Call
      // this.objectDetails = this.premiumCalculator.getMockObjectDetails();
      // this.premiumInfo = this.premiumCalculator.getMockPremiumInfo(this.objectDetails);

      },
    (err) => {
      console.log(err);
    });
  }

  _base64ToArrayBuffer(base64):any {

    var binary_string =  window.atob(base64);
    var len = binary_string.length;
    var bytes = new Uint8Array( len );

    for (var i = 0; i < len; i++)        {
      bytes[i] = binary_string.charCodeAt(i);
    }

    return bytes.buffer;
  }
}
