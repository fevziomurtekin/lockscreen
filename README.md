# Lock Screen

[![](https://jitpack.io/v/fevziomurtekin/lockscreen.svg)](https://jitpack.io/#fevziomurtekin/lockscreen)
 [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Lock%20screen-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7126)

Security screen using fingerprint or pin code. 

# Screens
Some Android devices do not have a fingerprint sensor. It is used for two different devices. For those with fingerprint sensors and those without.
<table>
  <tr>
    <th> For phones that do not have a fingerprint sensor</th>
    <th>For devices with a fingerprint sensor</th>
    <th>Fingerprint sensor for those who want to use the pincode.</th>
  </tr>
  <tr>
    <td>
      <center><img src="/screen/pincode.gif" width="250" height="350" /></center>
    </td>
    <td>
     <center><img src="/screen/fingerprint.gif" width="250" height="350" /></center>
    </td>
    <td>
     <center><img src="/screen/notusefingerprint.gif" width="250" height="350" /></center>
    </td>
  </tr>
</table>
</br>

# Usage

```Gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  .....

  dependencies {
	        implementation 'com.github.fevziomurtekin:lockscreen:0.1.1'
	  }
	}
  ```
  </br> Include in the activity 
  ```Gradle 
 public class MainActivity extends LookScreen {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setPass("1234");
        this.setDigitCount(4);
        this.setIntent(new Intent(this,SuccesActivity.class));
        this.setDigitCount(4);
        this.setTitle("YOUR TITLE MESSAGE");
        this.setTitleSize(15f);
        this.setTitleColor(getResources().getColor(R.color.black));
        this.setMessage("YOUR MESSAGE");
        this.setMessageSize(14f);
        this.setMessageColor(getResources().getColor(R.color.black));
        this.setError("YOUR ERROR MESSAGE");
        this.setErrorSize(14f);
        this.setErrorColor(getResources().getColor(R.color.red));
    }
}
  ```
  
  # Attributes

  | Attribute | Description |
| --- | --- |
| `title` | Default text, "Enter your password. " |
| `titleSize` | The size in sp of the title text size (by default 15sp) |
| `titleColor` | The color in int of the title text color (R.color.black) |
| `message` | The value in string of the message items (by default "Log in with your password or fingerprint reader.")  |
| `messageSize` |The size in sp of the message text size (by default 14sp) |
| `messageColor` | The color in int of the title text color (R.color.black) |
| `error` | The value in string of the error items (by default "You entered an incorrect password. Please try again.") |
| `errorSize` | The size in sp of the title text size (by default 14sp) |
| `errorColor` | The color in int of the title text color (R.color.red) |
| `intent` | It is for transferring to another class when successfully logged in |
| `pass` | The value in string of the password items (by default "1234") |


## License

    Copyright 2018 Fevzi Ömür Tekin
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



