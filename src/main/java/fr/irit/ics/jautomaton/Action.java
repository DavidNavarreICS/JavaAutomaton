/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.irit.ics.jautomaton;

/**
 *
 * @author David
 */
public interface Action {

    /**
     * Execute the action using the given parameters.
     *
     * @param parameters the parameters.
     */
    void execute(Object... parameters);
}
