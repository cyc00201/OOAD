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

 const getMemberId = async() => {

    try {

      const response = await Axios.get('http://localhost:9100/pvs-api/auth/memberId',
                                        {params:{username:username.trim()} })
      return response.data
    }
    catch (e) {
      alert(e.response?.status)
      console.error("mem " + e)
    }
  }

  const login = async() => {
    accountCheckingStart()
    if (!(username?.trim()?.length) || !(password?.toString()?.length)) {
      alert('不準啦馬的>///<')
      accountCheckingEnd()
      return
    }


    const jwt = await getJWTFrom()

    if (jwt && jwt !== '') {


      //localStorage.setItem('Authorization', jwt)
      //Axios.defaults.headers.common.Authorization = jwt

      const memberId  = 262;//= await getMemberId()

      if (memberId && memberId !== '') {
        localStorage.setItem('memberId', memberId)
        redirectToProjectSelectPage()
      }
      else {
        setAccountOperationHint('InvalidAccount')
      }
    }
    else {
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
