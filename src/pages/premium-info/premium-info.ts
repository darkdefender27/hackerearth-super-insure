import { Component } from '@angular/core';
import { NavController, NavParams } from 'ionic-angular';

@Component({
  selector: 'page-premium-info',
  templateUrl: 'premium-info.html'
})
export class PremiumInfoPage {

  constructor(public navCtrl: NavController, public navParams: NavParams) {}

  ionViewDidLoad() {
    console.log('ionViewDidLoad PremiumInfoPage');
  }

}
