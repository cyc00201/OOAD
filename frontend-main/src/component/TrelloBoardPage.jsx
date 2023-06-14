import { useEffect, useState } from 'react'
import { makeStyles } from '@mui/styles'
import { Backdrop, CircularProgress } from '@mui/material'
import Axios from 'axios'

import Board, { createTranslate } from 'react-trello'

const useStyles = makeStyles(theme => ({
  root: {
    marginLeft: '10px',
  },
  boardContainer: {
    'display': 'flex',
    '& > *': {
      margin: theme.spacing(1),
    },
    'minWidth': '30px',
  },
  backdrop: {
    zIndex: theme.zIndex.drawer + 1,
    color: '#fff',
  },
}))

function TrelloBoardPage() {
  const classes = useStyles()
  const [boardData, setBoardData] = useState({})
  const [hasBoardData, setHasBoardData] = useState(false)
  const [currentProject, setCurrentProject] = useState({})
  const projectId = localStorage.getItem('projectId')
  const jwtToken = localStorage.getItem('jwtToken')
  const memberId = localStorage.getItem('memberId')

  const [isLoading, setLoading] = useState(false)
  const loadingBoardEnd = () => {
    setLoading(false)
  }
  const loadingBoardStart = () => {
    setLoading(true)
  }

  const headers = { ...(jwtToken && { Authorization: jwtToken }) }

  const sendPVSBackendRequest = async(method, url, params) => {
    const baseURL = 'http://localhost:9100/pvs-api'
    const requestConfig = {
      baseURL,
      url,
      method,
      headers,
      params,
    }
    return (await Axios.request(requestConfig))?.data
  }

  const loadInitialProjectInfo = async() => {
    try {
      const response = await sendPVSBackendRequest('GET', `/project/${memberId}/${projectId}`)
      setCurrentProject(response)
    }
    catch (e) {
      alert(e.response?.status)
      console.error(e)
    }
  }



  useEffect(() => {
    loadInitialProjectInfo()
  }, [])

  const getTrelloData = async() => {
    const trelloBoard = currentProject.repositoryDTOList.find(repo => repo.type === 'trello')
    const url = trelloBoard.url
    if (trelloBoard !== undefined) {
      try {
        const response = await sendPVSBackendRequest('GET', '/trello/board', { url })
        setBoardData(response)
        setHasBoardData(true)
        loadingBoardEnd()
      }
      catch (e) {
        alert(e.response?.status)
        console.error(e)
        loadingBoardEnd()
      }
    }
  }

  const TEXTS = {
    'Add another lane': 'NEW LANE',
    'Click to add card': 'Click to add card',
    'Delete lane': 'Delete lane',
    'Lane actions': 'Lane actions',
     'placeholder': {
          title: 'title',
         description: 'Description',
         label: 'label',
        },
    'button': {
            'Add lane': 'Add Lane',
            'Add card': 'Add Card',
            'Cancel': 'Cancel',
    },
  }

  useEffect(() => {
    loadingBoardStart()
    if (Object.keys(currentProject).length !== 0)
      getTrelloData()
  }, [currentProject])

  return (
    <div className={ classes.root }>
      <Backdrop className={ classes.backdrop } open={ isLoading }>
        <CircularProgress color="inherit" />
      </Backdrop>
      <div className={ classes.boardContainer }>
        {hasBoardData
        && <Board
          data={ boardData }
         canAddLanes
          editable
          t={ createTranslate(TEXTS) }
          onCardUpdate={async function CardClick(cardId,changedata){
                                                        try{
                                                            const response = await sendPVSBackendRequest('POST', '/trello/updatecard',  {changedata} )
                                                            alert ("data updated")
                                                        }
                                                        catch(e){
                                                           alert("error!! Updatecard status:" + e.response?.status)
                                                            console.error(e)

                                                        }
          }}
          onCardAdd = {async function addCard(card,laneId){
                                              //  alert(JSON.stringify(card)) //show new card json
                                                try{
                                                    const response = await sendPVSBackendRequest('POST', '/trello/addcard',  {card,laneId} )
                                                    alert("add  the card on id:" + laneId + " lane")
                                                    loadInitialProjectInfo()
                                                 }
                                                catch(e){
                                                    alert("error!! Addcard status:" + e.response?.status)
                                                    console.error(e)
                                                    loadInitialProjectInfo()

                                                 }}}


          onCardDelete = {async function  deletecard(cardId){
                                            try{

                                                const response = await sendPVSBackendRequest('POST', '/trello/deletecard',  {cardId} )
                                                alert("delete  this card")
                                            }
                                            catch(e){
                                                 alert("error!! Deletecard status:" + e.response?.status)
                                                  console.error(e)

                                            }
                                    }
                         }
          onLaneAdd = {async function addLane(params){
                                    const trelloBoard = currentProject.repositoryDTOList.find(repo => repo.type === 'trello')
                                    const url = trelloBoard.url
                                    alert("New lane created!" + url)
                                            try{

                                                const response = await sendPVSBackendRequest('POST', '/trello/addlane',  {url,params} )

                                            }
                                            catch(e){
                                                 alert("error!! AddLane status:" + e.response?.status)
                                                  console.error(e)
                                                   loadInitialProjectInfo()


                                            }

                                    }}
          onLaneDelete = {function deleteLane(laneId){alert("delete L at " + laneId)}}

          handleDragEnd = {async function Dragcard(cardId, laneIdz, laneId, position, card){
                                            try{
                                                const response = await sendPVSBackendRequest('POST', '/trello/deletecard',  {cardId} )

                                            }
                                            catch(e){
                                                 alert("error!! Deletecard status:" + e.response?.status)
                                                  console.error(e)

                                            }



                                         try{

                                           const response = await sendPVSBackendRequest('POST', '/trello/addcard',  {card,laneId} )

                                           alert("Drag the card from " + laneIdz + "to " + laneId)
                                           loadInitialProjectInfo()
                                         }
                                         catch(e){
                                            alert("error!! Dragcard status:" + e.response?.status)
                                            console.error(e)
                                            loadInitialProjectInfo()

                                        }

          }}

        />
        }
      </div>

    </div>
  )
}

export default TrelloBoardPage
