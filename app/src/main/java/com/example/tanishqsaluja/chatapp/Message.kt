package com.example.tanishqsaluja.chatapp

/**
 * Created by tanishqsaluja on 18/2/18.
 */
data class Message(val message:String="",val type:String="",val time:Long=0,val seen:Boolean=false,val from:String="")