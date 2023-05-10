import { useState } from 'react'
import { Backdrop, Button, CircularProgress, TextField } from '@mui/material'
import { makeStyles } from '@mui/styles'
import Axios from 'axios'
import { useHistory } from 'react-router-dom'
import logo from '../assets/images/welcome.png'
import './Login.css'

const passwordRegex = /^(?=.*?[0-9])(?=.*[a-z])(?=.*[A-Z])(?=(?=.*?[`!@#$%^&*()_+-])|(?=.*?[=[\]{};'":|,.<>\/?~])).{8,}$/

export default function Login() {
  const useStyles = makeStyles(theme => ({
    root: {
      '& .MuiTextField-root': {
        margin: theme.spacing(1),
      },
    },
    backdrop: {
      zIndex: theme.zIndex.drawer + 1,
      color: '#fff',
    },
    accountOperationHint: {
      fontSize: '12px',
      color: '#FF0000',
    },
    accountOperationHintSuccess: {
      fontSize: '12px',
      color: '#00dc82',
    },
    registerButton: {
      marginRight: '1em !important',
    },
    actionButtonContainer: {
      display: 'flex',
    },
  }))

  const classes = useStyles()
  const history = useHistory()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [captcha,setCaptcha] = useState('')
   const [Pcaptcha,setPCaptcha] = useState('')
  const [accountOperationHint, setAccountOperationHint] = useState('')
  const [accountChecking, setAccountChecking] = useState(false)
  const accountCheckingEnd = () => {
    setPassword('')
    setAccountChecking(false)
  }
  const accountCheckingStart = () => {
    setAccountOperationHint('')
    setAccountChecking(true)
  }
   const getJWTFrom = async() => {
       //post login data
      const credential = {
            username: username.trim(),
            password,

      }

      try {
        const response = await Axios.post('http://localhost:9100/pvs-api/auth/login', credential)
        return response.data
      }
      catch (e) {
        console.warn("log " + e)
      }
    }

 const getMemberId = async(jwt) => {
    //getMemberId to get projectdata
    const config = {
      headers: {
         Authorization: jwt,
      },
      params: {
        username: username.trim(),
      },
    };
    try {
      // alert(jwt)
      const response = await Axios.get('http://localhost:9100/pvs-api/auth/memberId',config)
      return response.data
    }
    catch (e) {
      alert(e.response?.status)
      console.error("mem " + e)
    }
  }

  const login = async() => {
    accountCheckingStart()
    if (!(username?.trim()?.length) || !(password?.toString()?.length)||!(captcha?.trim()?.length )) {
      alert('不準啦馬的>///<')
      accountCheckingEnd()
      return
    }


    const jwt = await getJWTFrom()


    if (jwt && jwt !== '') {

      localStorage.setItem('jwtToken', jwt)
      Axios.defaults.headers.common.Authorization = jwt

      const memberId  =  await getMemberId(jwt)

      if (memberId && memberId !== '') {
        localStorage.setItem('memberId', memberId)

       /* if( captcha.trim() != Captcha0){
          alert(Captcha0)
           setAccountOperationHint('InvalidCaptcha')
        }
        else{*/
            //Success
            redirectToProjectSelectPage()
        //}
      }
      else {
        setAccountOperationHint('InvalidAccount')
      }
    }

    else{
      setAccountOperationHint('InvalidAccount')
    }


    accountCheckingEnd()
  }

  const register = async() => {
    accountCheckingStart()
    if (!(username?.trim()?.length) || !(password?.toString()?.length)) {
      alert('不準啦馬的>///<')
      accountCheckingEnd()
      return
    }

    if (!passwordRegex.test(password)) {
      alert('Password should contains: \n 1. More than 8 digits\n 2. At least one uppercase and lowercase character\n 3. At least one number\n 4. At least one symbol')
      accountCheckingEnd()
      return
    }
    const payload = {
      username: username?.trim(),
      password,
    }

    try {
      const { data } = await Axios.post('http://localhost:9100/pvs-api/auth/register', payload)
      setAccountOperationHint(data?.toString())
    }
    catch (e) {
      alert(e.response?.status)
      console.error("Reg" + e)
    }

    accountCheckingEnd()
  }
  var Captcha0= "0000";
 const generateCaptcha = async =>{

        const captchaLength = 5;
        const imageWidth = 150;
        const imageHeight = 80;
        const characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        // Generate a random string for the CAPTCHA
        var captcha = "";
        for (let i = 0; i < captchaLength; i++) {
         captcha += characters.charAt(Math.floor(Math.random() * characters.length));
        }
        Captcha0 = captcha
        // Save the CAPTCHA string to a session variable for later validation
        sessionStorage.setItem('captcha', captcha);

        // Get the canvas element and context and set its size
        const canvas = document.getElementById("captchaCanvas");
        canvas.width = imageWidth;
        canvas.height = imageHeight;
        const context = canvas.getContext("2d");

        // Create a blank image with a white background
        context.fillStyle = "#FFFFFF";
        context.fillRect(0, 0, imageWidth, imageHeight);

        // Add random lines and dots to the image for distortion
        for (let i = 0; i < 20; i++) {
          context.strokeStyle = "rgb(" + Math.floor(Math.random() * 256) + "," + Math.floor(Math.random() * 256) + "," + Math.floor(Math.random() * 256) + ")";
          context.beginPath();
          context.moveTo(Math.floor(Math.random() * imageWidth), Math.floor(Math.random() * imageHeight));
          context.lineTo(Math.floor(Math.random() * imageWidth), Math.floor(Math.random() * imageHeight));
          context.stroke();
        }
        for (let i = 0; i < 100; i++) {
          context.fillStyle = "rgb(" + Math.floor(Math.random() * 256) + "," + Math.floor(Math.random() * 256) + "," + Math.floor(Math.random() * 256) + ")";
          context.beginPath();
          context.arc(Math.floor(Math.random() * imageWidth), Math.floor(Math.random() * imageHeight), 1, 0, Math.PI * 2);
          context.fill();
        }

        // Add the CAPTCHA text to the image with random font, size, and angle
        const fontSize = 15;
        const angle = 0;
        const font = "Arial";
        context.font = fontSize + "px " + font;
        for (let i = 0; i < captchaLength; i++) {
          context.fillStyle = "rgb(" + Math.floor(Math.random() * 100) + "," + Math.floor(Math.random() * 100) + "," + Math.floor(Math.random() * 100) + ")";
          let x = i * (imageWidth / captchaLength) + Math.floor(Math.random() * (imageWidth / captchaLength - fontSize));
          let y = Math.floor(Math.random() * (imageHeight - fontSize)) + fontSize;
        	//console.log(x +"|" + y);

          context.fillText(captcha[i], x, y);
          context.rotate(angle);
          }

        return
 }



  const redirectToProjectSelectPage = () => {
    history.push('/select')
  }


  return (
    <div className={ classes.root }>
      <Backdrop className={ classes.backdrop } open={ accountChecking }>
        <CircularProgress color="inherit" />
      </Backdrop>
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo"/>

        {accountOperationHint === 'InvalidPassword'
          && <p className={ classes.accountOperationHint }>Invalid password</p>
        }
        {accountOperationHint === 'InvalidAccount'
          && <p className={ classes.accountOperationHint }>Invalid username or password</p>
        }
        {accountOperationHint === 'RegisterSuccess'
          && <p className={ classes.accountOperationHintSuccess }>Account is registered successfully</p>
        }
        {accountOperationHint === 'RegisterFailed'
          && <p className={ classes.accountOperationHint }>Account already exists</p>
        }

        {accountOperationHint === 'InvalidCaptcha'
                  && <p className={ classes.accountOperationHint }>'Incorrect Captcha'</p>
        }

        <TextField
          id="username"
          label="Username"
          type="text"
          variant="outlined"
          value={ username }
          onChange={ (e) => { setUsername(e.target.value) } }
        />

        <TextField
          id="password"
          label="Password"
          type="password"
          variant="outlined"
          value={ password }
          onChange={ (e) => { setPassword(e.target.value) } }
        />
        <br/>


        <canvas id = "captchaCanvas"></canvas>
        <Button variant="contained" onClick={ generateCaptcha } color="primary">
              generate
        </Button>
        <TextField
                  id="Captcha"
                  label="Captcha"
                  type="text"
                  variant="outlined"
                  value={ captcha }
                  onChange={ (e) => { setCaptcha(e.target.value) } }
                />
        <span className={ classes.actionButtonContainer }>
          <Button className={ classes.registerButton } variant="contained" onClick={ register } color="primary">
            Register
          </Button>
          <Button variant="contained" onClick={ login } color="primary">
            Login
          </Button>
        </span>

      </header>
    </div>
  )
}
